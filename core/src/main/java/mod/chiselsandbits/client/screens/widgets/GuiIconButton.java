package mod.chiselsandbits.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.chiselsandbits.api.client.screen.widget.AbstractChiselsAndBitsButton;
import mod.chiselsandbits.client.icon.IconManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GuiIconButton extends AbstractChiselsAndBitsButton
{
    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/button"), ResourceLocation.withDefaultNamespace("widget/button_disabled"), ResourceLocation.withDefaultNamespace("widget/button_highlighted")
    );
    public static final int SIZE = 20;
    TextureAtlasSprite icon;

	public GuiIconButton(
			final int x,
			final int y,
			final TextureAtlasSprite icon,
            Button.OnPress pressedAction,
            Tooltip tooltip)
	{
		super( x, y, SIZE, SIZE, Component.empty(), pressedAction, Button.DEFAULT_NARRATION);
		this.icon = icon;
        this.setTooltip(tooltip);
	}

    public GuiIconButton(
      final int x, final int y,
      final Component narration,
      final TextureAtlasSprite icon,
      final OnPress pressable)
    {
        super(x, y, SIZE, SIZE, narration, pressable, Button.DEFAULT_NARRATION);
        this.icon = icon;
    }

    public GuiIconButton(
      final int x, final int y,
      final Component narration,
      final TextureAtlasSprite icon,
      final OnPress pressable,
      final Tooltip tooltip)
    {
        super(x, y, SIZE, SIZE, narration, pressable, Button.DEFAULT_NARRATION);
        this.icon = icon;
        this.setTooltip(tooltip);
    }

    @Override
    public void renderWidget(final @NotNull GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTicks)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        guiGraphics.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        IconManager.getInstance().bindTexture();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        guiGraphics.blit(getX() + 2, getY() + 2, 0, 16,16, icon);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
    }
}
