package mod.chiselsandbits.client.ister;

import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.communi.suggestu.scena.core.registries.IPlatformRegistryManager;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.chiselsandbits.block.BitStorageBlock;
import mod.chiselsandbits.block.entities.BitStorageBlockEntity;
import mod.chiselsandbits.registrars.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BitStorageISTER extends BlockEntityWithoutLevelRenderer
{
    private static final ItemTransform GUI = new ItemTransform(
      new Vector3f(30,225,0),
      new Vector3f(-0.5f, 0f, 0),
      new Vector3f(0.625f, 0.625f, 0.625f)
    );

    public BitStorageISTER()
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
          Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(
      final @NotNull ItemStack stack,
      final @NotNull ItemDisplayContext transformType,
      final PoseStack matrixStack,
      final @NotNull MultiBufferSource buffer,
      final int combinedLight,
      final int combinedOverlay)
    {

        final BakedModel model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(
          IPlatformRegistryManager.getInstance().getBlockRegistry()
            .getKey(ModBlocks.BIT_STORAGE.get())
          , "facing=east"));


        final BitStorageBlockEntity blockEntity = BitStorageBlock.createEntityFromStack(stack);

        matrixStack.pushPose();

        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, matrixStack, buffer, combinedLight, combinedOverlay);

        matrixStack.popPose();


        matrixStack.pushPose();

        IRenderingManager.getInstance()
                .renderModel(matrixStack.last(), buffer.getBuffer(RenderType.cutoutMipped()), ModBlocks.BIT_STORAGE
                        .get().defaultBlockState(), model, 1f, 1f, 1f, combinedLight, combinedOverlay, RenderType.cutout());

        matrixStack.popPose();
    }
}
