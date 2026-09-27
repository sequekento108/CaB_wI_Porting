package mod.chiselsandbits.client.screens.widgets;

import com.communi.suggestu.scena.core.util.TransformationUtils;
import com.mojang.blaze3d.platform.Window;
import mod.chiselsandbits.api.client.screen.widget.AbstractChiselsAndBitsWidget;
import mod.chiselsandbits.api.item.multistate.IMultiStateItem;
import mod.chiselsandbits.api.multistate.snapshot.IMultiStateSnapshot;
import mod.chiselsandbits.api.util.ColorUtils;
import mod.chiselsandbits.multistate.snapshot.EmptySnapshot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
public class MultiStateSnapshotWidget extends AbstractChiselsAndBitsWidget
{

    private ItemStack snapshotBlockStack = ItemStack.EMPTY;

    private Vec3 facingVector = Vec3.ZERO;
    private float scaleFactor = 1f;

    public MultiStateSnapshotWidget(final int x, final int y, final int width, final int height, final Component title)
    {
        super(x, y, width, height, title);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderWidget(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks)
    {
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, ColorUtils.pack(139));
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width - 1, this.getY() + this.height - 1, ColorUtils.pack(55));
        graphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width, this.getY() + this.height, ColorUtils.pack(ColorUtils.FULL_CHANNEL));
        graphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, ColorUtils.pack(ColorUtils.EMPTY_CHANNEL));

        scissorStart();

        if (!snapshotBlockStack.isEmpty()) {
            graphics.pose().pushPose();
            renderRotateableItemAndEffectIntoGui(graphics);
            graphics.pose().popPose();
        }

        scissorEnd();
    }

    @SuppressWarnings({"deprecation", "ConstantConditions"})
    public void renderRotateableItemAndEffectIntoGui(@NotNull GuiGraphics graphics) {
        final int x = this.getX() + this.width / 2 - 8;
        final int y = this.getY() + this.height / 2 - 8;

        graphics.pose().pushPose();
        graphics.pose().translate((float)x, (float)y, 150);
        graphics.pose().mulPose(TransformationUtils.quatFromXYZ(this.facingVector.toVector3f(), false));
        graphics.pose().scale(scaleFactor, scaleFactor, scaleFactor);
        graphics.pose().translate(-8.0F, -8.0F, 0.0F);

        graphics.renderItem(snapshotBlockStack, 0, 0);

        graphics.pose().popPose();
    }

    public IMultiStateSnapshot getSnapshot()
    {
        if (!(snapshotBlockStack.getItem() instanceof IMultiStateItem))
            return EmptySnapshot.INSTANCE;

        return ((IMultiStateItem) snapshotBlockStack.getItem()).createItemStack(snapshotBlockStack).createSnapshot();
    }

    public void setSnapshot(final IMultiStateSnapshot snapshot)
    {
        this.snapshotBlockStack = snapshot.toItemStack().toBlockStack();
    }

    @Override
    protected void onDrag(final double mouseX, final double mouseY, final double dragX, final double dragY)
    {
        this.facingVector = this.facingVector.add(-dragY * 10, dragX * 10, 0);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double deltaX, final double deltaY)
    {
        this.scaleFactor += deltaY * 0.25;
        return true;
    }

    private void scissorStart()
    {
        Window mw = Minecraft.getInstance().getWindow();
        double sf = mw.getGuiScale();
        GL11.glScissor((int)((this.getX() + 1) * mw.getGuiScale()), (int)(mw.getGuiScaledHeight() * sf - height * sf - (getY() - 1) * sf), (int)((width - 2) * sf), (int)((height - 1) * sf));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    protected void scissorEnd() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
