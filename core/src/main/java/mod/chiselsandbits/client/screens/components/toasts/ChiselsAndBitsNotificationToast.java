package mod.chiselsandbits.client.screens.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.chiselsandbits.api.util.IWithColor;
import mod.chiselsandbits.api.util.IWithIcon;
import mod.chiselsandbits.api.util.IWithText;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChiselsAndBitsNotificationToast<T extends IWithColor & IWithIcon & IWithText> implements Toast
{
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
    private final T contents;

    public static <G extends IWithColor & IWithIcon & IWithText> void notifyOf(G contents) {
        Minecraft.getInstance().getToasts().addToast(new ChiselsAndBitsNotificationToast<>(contents));
    }

    private ChiselsAndBitsNotificationToast(final T contents) {this.contents = contents;}

    @Override
    public @NotNull Visibility render(final @NotNull GuiGraphics guiGraphics, final @NotNull ToastComponent toastComponent, final long time)
    {
        guiGraphics.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());

        List<FormattedCharSequence> list = toastComponent.getMinecraft().font.split(contents.getText(), 125);
        if (list.size() == 1)
        {
            guiGraphics.drawString(toastComponent.getMinecraft().font, contents.getText(), 30, 12, -1);
        }
        else
        {
            int fontColor = Mth.floor(Mth.clamp((float) (time) / 40.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
            int verticalOffset = this.height() / 2 - list.size() * 9 / 2;

            for (FormattedCharSequence formattedcharsequence : list)
            {
                guiGraphics.drawString(toastComponent.getMinecraft().font, formattedcharsequence, 30, verticalOffset, 16777215 | fontColor);
                verticalOffset += 9;
            }
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(8,8,0);
        guiGraphics.pose().pushPose();

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(
          (float) contents.getColorVector().x(),
          (float) contents.getColorVector().y(),
          (float) contents.getColorVector().z(),
          (float) contents.getAlphaChannel()
        );
        RenderSystem.setShaderTexture(0, contents.getIcon());
        guiGraphics.blit(contents.getIcon(), 0, 0, 16,16, 0, 0, 18, 18, 18, 18);

        guiGraphics.pose().popPose();
        guiGraphics.pose().popPose();

        return time >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}
