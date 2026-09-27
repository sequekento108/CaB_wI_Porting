package mod.chiselsandbits.client.model.data;

import com.communi.suggestu.scena.core.client.models.data.IModelDataBuilder;
import com.communi.suggestu.scena.core.client.models.data.IModelDataManager;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.mojang.logging.LogUtils;
import mod.chiselsandbits.ChiselsAndBits;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.config.IClientConfiguration;
import mod.chiselsandbits.api.multistate.accessor.IAreaAccessor;
import mod.chiselsandbits.api.neighborhood.IBlockNeighborhood;
import mod.chiselsandbits.api.neighborhood.IBlockNeighborhoodBuilder;
import mod.chiselsandbits.api.profiling.IProfilerSection;
import mod.chiselsandbits.api.variant.state.IStateVariant;
import mod.chiselsandbits.api.variant.state.IStateVariantManager;
import mod.chiselsandbits.block.entities.ChiseledBlockEntity;
import mod.chiselsandbits.client.model.baked.chiseled.ChiselRenderType;
import mod.chiselsandbits.client.model.baked.chiseled.ChiseledBlockBakedModel;
import mod.chiselsandbits.client.model.baked.chiseled.ChiseledBlockBakedModelManager;
import mod.chiselsandbits.client.model.baked.chiseled.FluidRenderingManager;
import mod.chiselsandbits.client.model.baked.simple.CombinedModel;
import mod.chiselsandbits.client.model.baked.simple.NullBakedModel;
import mod.chiselsandbits.client.multistate.rendering.RenderingAreaAccessor;
import mod.chiselsandbits.client.util.BlockInformationUtils;
import mod.chiselsandbits.profiling.ProfilingManager;
import mod.chiselsandbits.registrars.ModModelProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ChiseledBlockModelDataExecutor {
    private static ExecutorService recalculationService;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void updateModelDataCore(final ChiseledBlockEntity tileEntity, final Runnable onCompleteCallback) {
        ensureThreadPoolSetup();

        final IBlockNeighborhood neighborhood = IBlockNeighborhoodBuilder.getInstance().build(
                direction -> {
                    final BlockState state = Objects.requireNonNull(tileEntity.getLevel()).getBlockState(tileEntity.getBlockPos().offset(direction.getNormal()));
                    final Optional<IStateVariant> additionalStateInfo = IStateVariantManager.getInstance().getStateVariant(
                            state,
                            Optional.ofNullable(tileEntity.getLevel().getBlockEntity(tileEntity.getBlockPos().offset(direction.getNormal())))
                    );

                    return new BlockInformation(state, additionalStateInfo);
                },
                direction -> {
                    final BlockEntity otherTileEntity = Objects.requireNonNull(tileEntity.getLevel()).getBlockEntity(tileEntity.getBlockPos().offset(direction.getNormal()));
                    if (otherTileEntity instanceof IAreaAccessor) {
                        return (IAreaAccessor) otherTileEntity;
                    }

                    return null;
                }
        );
        CompletableFuture.supplyAsync(() -> {
                    BakedModel unknownRenderTypeModel;
                    Map<RenderType, BakedModel> renderTypedModels = Maps.newLinkedHashMap();

                    final Set<RenderType> renderTypes = BlockInformationUtils.extractRenderTypes(tileEntity.getStatistics().getStateCounts().keySet());

                    try (IProfilerSection ignored1 = ProfilingManager.getInstance().withSection("Extract model data from data")) {

                        try (IProfilerSection ignored2 = ProfilingManager.getInstance().withSection("Known render layer model building")) {
                            for (final RenderType chunkBufferLayer : renderTypes) {
                                final ChiselRenderType solidType =
                                        ChiselRenderType.fromLayer(chunkBufferLayer, false);
                                final ChiselRenderType fluidType =
                                        ChiselRenderType.fromLayer(chunkBufferLayer, true);

                                if (tileEntity.getStatistics().getStateCounts().isEmpty() ||
                                        (tileEntity.getStatistics().getStateCounts().size() == 1 && tileEntity.getStatistics().getStateCounts().containsKey(BlockInformation.AIR))) {
                                    continue;
                                }

                                BakedModel baked;

                                try (IProfilerSection ignored3 = ProfilingManager.getInstance()
                                        .withSection("Known render layer model building for: " + solidType.name() + " and " + fluidType.name())) {

                                    if (FluidRenderingManager.getInstance().isFluidRenderType(chunkBufferLayer)) {
                                        try (IProfilerSection ignored4 = ProfilingManager.getInstance().withSection("Combined model building")) {

                                            final ChiseledBlockBakedModel solidModel;
                                            try (IProfilerSection ignored5 = ProfilingManager.getInstance().withSection("Solid")) {
                                                solidModel = ChiseledBlockBakedModelManager.getInstance().get(
                                                        tileEntity,
                                                        tileEntity.getStatistics().getPrimaryState(),
                                                        solidType,
                                                        neighborhood,
                                                        tileEntity.getBlockPos(),
                                                        chunkBufferLayer
                                                );
                                            }

                                            final ChiseledBlockBakedModel fluidModel;
                                            try (IProfilerSection ignored5 = ProfilingManager.getInstance().withSection("Fluid")) {
                                                fluidModel = ChiseledBlockBakedModelManager.getInstance().get(
                                                        tileEntity,
                                                        tileEntity.getStatistics().getPrimaryState(),
                                                        fluidType,
                                                        neighborhood,
                                                        tileEntity.getBlockPos(),
                                                        chunkBufferLayer
                                                );
                                            }

                                            try (IProfilerSection ignored5 = ProfilingManager.getInstance().withSection("Model combining")) {
                                                if (solidModel.isEmpty()) {
                                                    baked = fluidModel;
                                                } else if (fluidModel.isEmpty()) {
                                                    baked = solidModel;
                                                } else {
                                                    baked = new CombinedModel(solidModel, fluidModel);
                                                }
                                            }
                                        }
                                    } else {
                                        try (IProfilerSection ignored4 = ProfilingManager.getInstance().withSection("Singular model building")) {
                                            baked = ChiseledBlockBakedModelManager.getInstance().get(
                                                    tileEntity,
                                                    tileEntity.getStatistics().getPrimaryState(),
                                                    ChiselRenderType.fromLayer(chunkBufferLayer, false),
                                                    neighborhood,
                                                    tileEntity.getBlockPos(),
                                                    chunkBufferLayer
                                            );
                                        }
                                    }
                                }

                                renderTypedModels.put(chunkBufferLayer, baked);
                            }
                        }
                    }

                    unknownRenderTypeModel = new CombinedModel(renderTypedModels.values().stream()
                            .filter(model -> model != NullBakedModel.instance)
                            .toArray(BakedModel[]::new));

                    return IModelDataBuilder.create()
                            .withInitial(
                                    ModModelProperties.UNKNOWN_LAYER_MODEL_PROPERTY, unknownRenderTypeModel
                            )
                            .withInitial(
                                    ModModelProperties.KNOWN_LAYER_MODEL_PROPERTY, renderTypedModels
                            )
                            .build();
                }, recalculationService)
                .thenAcceptAsync(tileEntity::setModelData, recalculationService)
                .thenRunAsync(onCompleteCallback, recalculationService)
                .thenRunAsync(() -> {
                    try {
                        IModelDataManager.getInstance().requestModelDataRefresh(tileEntity);
                        if (tileEntity.getLevel() != null) {
                            tileEntity.getLevel().sendBlockUpdated(
                                    tileEntity.getBlockPos(),
                                    tileEntity.getBlockState(),
                                    tileEntity.getBlockState(),
                                    8
                            );
                        }
                    } catch (Exception ignored) {
                    }
                }, Minecraft.getInstance())
                .exceptionally(throwable -> {
                    LOGGER.error("Failed to update model data for chiseled block entity", throwable);
                    return null;
                });
    }

    public static void updateModelDataPerContainedState(final ChiseledBlockEntity tileEntity, final Consumer<Table<RenderType, BlockInformation, BakedModel>> resultConsumer) {
        ensureThreadPoolSetup();

        final IBlockNeighborhood neighborhood = IBlockNeighborhoodBuilder.getInstance().build(
                direction -> {
                    final BlockState state = Objects.requireNonNull(tileEntity.getLevel()).getBlockState(tileEntity.getBlockPos().offset(direction.getNormal()));
                    final Optional<IStateVariant> additionalStateInfo = IStateVariantManager.getInstance().getStateVariant(
                            state,
                            Optional.ofNullable(tileEntity.getLevel().getBlockEntity(tileEntity.getBlockPos().offset(direction.getNormal())))
                    );

                    return new BlockInformation(state, additionalStateInfo);
                },
                direction -> {
                    final BlockEntity otherTileEntity = Objects.requireNonNull(tileEntity.getLevel()).getBlockEntity(tileEntity.getBlockPos().offset(direction.getNormal()));
                    if (otherTileEntity instanceof IAreaAccessor) {
                        return (IAreaAccessor) otherTileEntity;
                    }

                    return null;
                }
        );

        CompletableFuture.supplyAsync(new Supplier<Table<RenderType, BlockInformation, BakedModel>>() {
                    @Override
                    public Table<RenderType, BlockInformation, BakedModel> get() {
                        final HashBasedTable<RenderType, BlockInformation, BakedModel> result = HashBasedTable.create();

                        for (BlockInformation blockInformation : tileEntity.getStatistics().getContainedStates()) {
                            final Set<RenderType> renderTypes = BlockInformationUtils.extractRenderTypes(blockInformation);

                            final IAreaAccessor filtered = new RenderingAreaAccessor(blockInformation, tileEntity);

                            for (RenderType renderType : renderTypes) {
                                final ChiselRenderType solidType =
                                        ChiselRenderType.fromLayer(renderType, false);
                                final ChiselRenderType fluidType =
                                        ChiselRenderType.fromLayer(renderType, true);

                                if (tileEntity.getStatistics().getStateCounts().isEmpty() ||
                                        (tileEntity.getStatistics().getStateCounts().size() == 1 && tileEntity.getStatistics().getStateCounts().containsKey(BlockInformation.AIR))) {
                                    continue;
                                }

                                BakedModel baked;

                                try (IProfilerSection ignored3 = ProfilingManager.getInstance()
                                        .withSection("Known render layer model building for: " + solidType.name() + " and " + fluidType.name())) {

                                    if (FluidRenderingManager.getInstance().isFluidRenderType(renderType)) {
                                        try (IProfilerSection ignored4 = ProfilingManager.getInstance().withSection("Combined model building")) {

                                            final ChiseledBlockBakedModel solidModel;
                                            try (IProfilerSection ignored5 = ProfilingManager.getInstance().withSection("Solid")) {
                                                solidModel = ChiseledBlockBakedModelManager.getInstance().get(
                                                        filtered,
                                                        blockInformation,
                                                        solidType,
                                                        neighborhood,
                                                        tileEntity.getBlockPos(),
                                                        renderType
                                                );
                                            }

                                            final ChiseledBlockBakedModel fluidModel;
                                            try (IProfilerSection ignored5 = ProfilingManager.getInstance().withSection("Fluid")) {
                                                fluidModel = ChiseledBlockBakedModelManager.getInstance().get(
                                                        filtered,
                                                        blockInformation,
                                                        fluidType,
                                                        neighborhood,
                                                        tileEntity.getBlockPos(),
                                                        renderType
                                                );
                                            }

                                            try (IProfilerSection ignored5 = ProfilingManager.getInstance().withSection("Model combining")) {
                                                if (solidModel.isEmpty()) {
                                                    baked = fluidModel;
                                                } else if (fluidModel.isEmpty()) {
                                                    baked = solidModel;
                                                } else {
                                                    baked = new CombinedModel(solidModel, fluidModel);
                                                }
                                            }
                                        }
                                    } else {
                                        try (IProfilerSection ignored4 = ProfilingManager.getInstance().withSection("Singular model building")) {
                                            baked = ChiseledBlockBakedModelManager.getInstance().get(
                                                    filtered,
                                                    blockInformation,
                                                    ChiselRenderType.fromLayer(renderType, false),
                                                    neighborhood,
                                                    tileEntity.getBlockPos(),
                                                    renderType
                                            );
                                        }
                                    }
                                }

                                result.put(renderType, blockInformation, baked);
                            }
                        }

                        return result;
                    }
                }, recalculationService)
                .thenAcceptAsync(resultConsumer, recalculationService);
    }

    private static synchronized void ensureThreadPoolSetup() {
        if (recalculationService == null) {
            final ClassLoader classLoader = ChiselsAndBits.class.getClassLoader();
            final AtomicInteger genericThreadCounter = new AtomicInteger();
            recalculationService = Executors.newFixedThreadPool(
                    IClientConfiguration.getInstance().getModelBuildingThreadCount().get(),
                    runnable -> {
                        final Thread thread = new Thread(runnable);
                        thread.setContextClassLoader(classLoader);
                        thread.setName(String.format("Chisels and Bits Model builder #%s", genericThreadCounter.incrementAndGet()));
                        thread.setDaemon(true);
                        return thread;
                    }
            );
        }
    }
}
