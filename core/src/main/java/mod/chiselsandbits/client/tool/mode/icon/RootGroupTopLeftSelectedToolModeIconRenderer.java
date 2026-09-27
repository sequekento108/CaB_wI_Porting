package mod.chiselsandbits.client.tool.mode.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.chiselsandbits.api.client.tool.mode.icon.ISelectedToolModeIconRenderer;
import mod.chiselsandbits.api.item.withmode.IRenderableMode;
import mod.chiselsandbits.api.item.withmode.IToolMode;
import mod.chiselsandbits.api.item.withmode.IWithModeItem;
import mod.chiselsandbits.api.util.constants.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

public class RootGroupTopLeftSelectedToolModeIconRenderer implements ISelectedToolModeIconRenderer
{
    static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "group");

    @Override
    public ResourceLocation getId()
    {
        return ID;
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final ItemStack stack)
    {
        if (!(stack.getItem() instanceof final IWithModeItem<?> modeItem))
            return;

        final IToolMode<?> mode = modeItem.getMode(stack);
        final IRenderableMode renderableMode = getRootRenderableMode(mode);

        final Vec2 positionVector = renderableMode.getPositionVector();
        final Vec2 scaleVector = renderableMode.getScaleVector();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(positionVector.x, positionVector.y, 1000);
        guiGraphics.pose().scale(scaleVector.x, scaleVector.y, 1);
        guiGraphics.pose().pushPose();

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(
          (float) renderableMode.getColorVector().x(),
          (float) renderableMode.getColorVector().y(),
          (float) renderableMode.getColorVector().z(),
          (float) renderableMode.getAlphaChannel()
        );
        RenderSystem.setShaderTexture(0, mode.getIcon());
        guiGraphics.blit(mode.getIcon(), 0, 0, 16,16, 0, 0, 18, 18, 18, 18);

        guiGraphics.pose().popPose();
        guiGraphics.pose().popPose();
    }

    private IRenderableMode getRootRenderableMode(final IRenderableMode mode) {
        if (mode instanceof IToolMode && ((IToolMode<?>) mode).getGroup().isPresent())
        {
            return ((IToolMode<?>) mode).getGroup().get();
        }

        return mode;
    }
}
