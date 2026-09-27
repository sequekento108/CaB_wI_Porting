package mod.chiselsandbits.client.besr;

import com.communi.suggestu.scena.core.client.fluid.IClientFluidManager;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.core.fluid.IFluidManager;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.chiselsandbits.api.block.storage.StateEntryStorage;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.config.IClientConfiguration;
import mod.chiselsandbits.block.entities.BitStorageBlockEntity;
import mod.chiselsandbits.client.model.baked.chiseled.ChiselRenderType;
import mod.chiselsandbits.client.model.baked.chiseled.ChiseledBlockBakedModel;
import mod.chiselsandbits.client.model.baked.chiseled.ChiseledBlockBakedModelManager;
import mod.chiselsandbits.client.util.BlockInformationUtils;
import mod.chiselsandbits.client.util.FluidCuboidUtils;
import mod.chiselsandbits.utils.MultiStateSnapshotUtils;
import mod.chiselsandbits.utils.SimpleMaxSizedCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BitStorageBESR implements BlockEntityRenderer<BitStorageBlockEntity> {
    private static final SimpleMaxSizedCache<CacheKey, StateEntryStorage> STORAGE_CONTENTS_BLOB_CACHE = new SimpleMaxSizedCache<>(IClientConfiguration.getInstance().getBitStorageContentCacheSize()::get);

    public BitStorageBESR() {
    }

    public static void clearCache() {
        STORAGE_CONTENTS_BLOB_CACHE.clear();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public void render(final BitStorageBlockEntity te, final float partialTicks, final @NotNull PoseStack poseStack, final @NotNull MultiBufferSource buffer, final int combinedLightIn, final int combinedOverlayIn) {
        if (te.containsFluid()) {
            final FluidInformation fluidStack = te.getFluid().get();
            RenderType.chunkBufferLayers().forEach(renderType -> {
                if (!IRenderTypeManager.getInstance().canRenderInType(fluidStack.fluid().defaultFluidState(), renderType))
                    return;

                if (renderType == RenderType.translucent() && Minecraft.useShaderTransparency())
                    renderType = Sheets.translucentCullBlockSheet();

                final VertexConsumer builder = buffer.getBuffer(renderType);

                final float fullness = (float) fluidStack.amount() / (float) IFluidManager.getInstance().getBucketAmount();

                final float heightFactor = Math.min(Math.max(0, fullness), 1f);

                FluidCuboidUtils.renderScaledFluidCuboid(fluidStack, poseStack, builder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 15, 15 * heightFactor, 15);
            });

            return;
        }

        final int bits = te.getBits();
        final BlockInformation blockInformation = te.getContainedBlockInformation();
        if (bits <= 0 || blockInformation == null) return;

        final CacheKey cacheKey = new CacheKey(blockInformation, bits);
        StateEntryStorage innerModelBlob = STORAGE_CONTENTS_BLOB_CACHE.get(cacheKey);
        if (innerModelBlob == null) {
            innerModelBlob = new StateEntryStorage();

            innerModelBlob.fillFromBottom(blockInformation, bits);
            STORAGE_CONTENTS_BLOB_CACHE.put(cacheKey, innerModelBlob);
        }

        poseStack.pushPose();
        poseStack.translate(2 / 16f, 2 / 16f, 2 / 16f);
        poseStack.scale(12 / 16f, 12 / 16f, 12 / 16f);
        final StateEntryStorage finalInnerModelBlob = innerModelBlob;
        final Set<RenderType> renderTypes = BlockInformationUtils.extractRenderTypes(blockInformation);
        renderTypes.forEach(renderType -> {
            final ChiseledBlockBakedModel innerModel = ChiseledBlockBakedModelManager.getInstance().get(MultiStateSnapshotUtils.createFromStorage(finalInnerModelBlob), blockInformation, ChiselRenderType.fromLayer(renderType, te.containsFluid()), null, null, te.getBlockPos(), renderType);

            if (!innerModel.isEmpty()) {
                float r;
                float g;
                float b;
                if (te.containsFluid()) {
                    r = te.getFluid().map(IClientFluidManager.getInstance()::getFluidColor).map(color -> (color >> 16) & 0xff).orElse(255) / 255f;
                    g = te.getFluid().map(IClientFluidManager.getInstance()::getFluidColor).map(color -> (color >> 8) & 0xff).orElse(255) / 255f;
                    b = te.getFluid().map(IClientFluidManager.getInstance()::getFluidColor).map(color -> color & 0xff).orElse(255) / 255f;
                } else {
                    final int color = Minecraft.getInstance().itemColors.getColor(new ItemStack(blockInformation.blockState().getBlock()), 0);
                    r = ((color >> 16) & 0xff) / 255f;
                    g = ((color >> 8) & 0xff) / 255f;
                    b = (color & 0xff) / 255f;
                }

                IRenderingManager.getInstance().renderModel(poseStack.last(), buffer.getBuffer(renderType), blockInformation.blockState(), innerModel, r, g, b, combinedLightIn, combinedOverlayIn, renderType);
            }
        });
        poseStack.popPose();
    }

    private record CacheKey(BlockInformation blockInformation, int bitCount) {}
}
