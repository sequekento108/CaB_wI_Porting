package mod.chiselsandbits.client.model.baked.chiseled;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import mod.chiselsandbits.api.config.IClientConfiguration;
import mod.chiselsandbits.api.item.multistate.IMultiStateItem;
import mod.chiselsandbits.api.item.multistate.IMultiStateItemStack;
import mod.chiselsandbits.api.profiling.IProfilerSection;
import mod.chiselsandbits.client.model.baked.base.BaseBakedBlockModel;
import mod.chiselsandbits.client.model.baked.base.BaseSmartModel;
import mod.chiselsandbits.client.model.baked.simple.CombinedModel;
import mod.chiselsandbits.client.model.baked.simple.NullBakedModel;
import mod.chiselsandbits.client.util.BlockInformationUtils;
import mod.chiselsandbits.profiling.ProfilingManager;
import mod.chiselsandbits.registrars.ModModelProperties;
import mod.chiselsandbits.utils.SimpleMaxSizedCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Stream;

@SuppressWarnings("ConstantConditions")
public class DataAwareChiseledBlockBakedModel extends BaseSmartModel
{
    private static final SimpleMaxSizedCache<Tag, BakedModel> STACK_MODEL_CACHE = new SimpleMaxSizedCache<>(
      IClientConfiguration.getInstance().getStackModelCacheSize()::get
    );

    @Override
    public boolean useAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean usesBlockLight()
    {
        return true;
    }

    @Override
    public BakedModel handleBlockState(BlockState state, RandomSource random, IBlockModelData modelData) {
        if (!modelData.hasProperty(ModModelProperties.UNKNOWN_LAYER_MODEL_PROPERTY))
            return NullBakedModel.instance;

        return modelData.getData(ModModelProperties.UNKNOWN_LAYER_MODEL_PROPERTY);
    }

    @Override
    public BakedModel handleBlockState(final BlockState state, final RandomSource random, final IBlockModelData modelData, @Nullable RenderType renderType)
    {
        if (!modelData.hasProperty(ModModelProperties.KNOWN_LAYER_MODEL_PROPERTY) && !modelData.hasProperty(ModModelProperties.UNKNOWN_LAYER_MODEL_PROPERTY))
            return NullBakedModel.instance;

        if (!modelData.hasProperty(ModModelProperties.KNOWN_LAYER_MODEL_PROPERTY) || renderType == null)
            return modelData.getData(ModModelProperties.UNKNOWN_LAYER_MODEL_PROPERTY);

        return modelData.getData(ModModelProperties.KNOWN_LAYER_MODEL_PROPERTY).getOrDefault(
          renderType,
          NullBakedModel.instance
        );
    }

    @Override
    public @NotNull Collection<RenderType> getSupportedRenderTypes(BlockState blockState, RandomSource randomSource, IBlockModelData iBlockModelData) {
        if (!iBlockModelData.hasProperty(ModModelProperties.KNOWN_LAYER_MODEL_PROPERTY)) {
            return super.getSupportedRenderTypes(blockState, randomSource, iBlockModelData);
        }

        return iBlockModelData.getData(ModModelProperties.KNOWN_LAYER_MODEL_PROPERTY).keySet();
    }

    @Override
    public BakedModel handleItemStack(ItemStack stack) {
        final Item item = stack.getItem();
        if (!(item instanceof final IMultiStateItem multiStateItem))
            return NullBakedModel.instance;

        if (Minecraft.getInstance().level == null)
            return buildModel(stack, multiStateItem);

        final Tag cacheKey = stack.save(Minecraft.getInstance().level.registryAccess());
        return STACK_MODEL_CACHE.get(cacheKey, () -> buildModel(stack, multiStateItem));
    }

    private static @NotNull BaseBakedBlockModel buildModel(ItemStack stack, IMultiStateItem multiStateItem) {
        final IMultiStateItemStack multiStateItemStack = multiStateItem.createItemStack(stack);

        final BakedModel[] typedModels;
        try(IProfilerSection ignored1 = ProfilingManager.getInstance().withSection("Building individual render type models"))
        {
            typedModels = BlockInformationUtils.extractRenderTypes(multiStateItemStack.getStatistics().getContainedStates())
                    .stream()
                    .flatMap(type -> {
                        final BakedModel fluidModel = ChiseledBlockBakedModelManager.getInstance().get(multiStateItemStack, ChiselRenderType.fromLayer(type, true), type);
                        final BakedModel solidModel = ChiseledBlockBakedModelManager.getInstance().get(multiStateItemStack, ChiselRenderType.fromLayer(type, false), type);
                        return Stream.of(fluidModel, solidModel);
                    })
                    .toArray(BakedModel[]::new);
        }

        if (typedModels.length == 0)
            return ChiseledBlockBakedModel.EMPTY;

        try(IProfilerSection ignored1 = ProfilingManager.getInstance().withSection("Combining model data"))
        {
            return new CombinedModel(typedModels);
        }
    }
}
