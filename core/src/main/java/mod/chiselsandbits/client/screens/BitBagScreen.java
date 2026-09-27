package mod.chiselsandbits.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.chiselsandbits.ChiselsAndBits;
import mod.chiselsandbits.api.util.LocalStrings;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.client.icon.IconManager;
import mod.chiselsandbits.client.screens.widgets.GuiIconButton;
import mod.chiselsandbits.container.BagContainer;
import mod.chiselsandbits.network.packets.ClearBagGuiPacket;
import mod.chiselsandbits.network.packets.ConvertBagGuiPacket;
import mod.chiselsandbits.network.packets.SortBagGuiPacket;
import mod.chiselsandbits.registrars.ModItems;
import mod.chiselsandbits.slots.BitSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4fStack;

public class BitBagScreen extends AbstractContainerScreen<BagContainer> {

    private static final ResourceLocation BAG_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/bitbag.png");

    boolean requireConfirm = true;
    boolean dontThrow = false;
    private GuiIconButton trashBtn;
    private GuiIconButton sortBtn;
    private GuiIconButton convertBtn;
    private Slot hoveredBitSlot = null;

    public BitBagScreen(
            final BagContainer container,
            final Inventory playerInventory,
            final Component title
    ) {
        super(container, playerInventory, title);
        imageHeight = 239;
    }

    @Override
    protected void init() {
        super.init();

        trashBtn = addRenderableWidget(new GuiIconButton(leftPos - 20, topPos, LocalStrings.Trash.getText(), IconManager.getInstance().getTrashIcon(),
                button -> {
                    if (requireConfirm) {
                        dontThrow = true;
                        if (isValidBitItem()) {
                            requireConfirm = false;
                        }
                    } else {
                        requireConfirm = true;
                        // server side!
                        final ClearBagGuiPacket packet = new ClearBagGuiPacket(getInHandItem());
                        ChiselsAndBits.getInstance().getNetworkChannel().sendToServer(packet);
                        packet.execute(Minecraft.getInstance().player);
                    }
                }));

        sortBtn = addRenderableWidget(new GuiIconButton(leftPos - 20, topPos + 22, LocalStrings.Sort.getText(), IconManager.getInstance().getSortIcon(),
                button -> {
                    final SortBagGuiPacket packet = new SortBagGuiPacket();
                    ChiselsAndBits.getInstance().getNetworkChannel().sendToServer(packet);
                    packet.execute(Minecraft.getInstance().player);
                },
                Tooltip.create(LocalStrings.Sort.getText())));

        convertBtn = addRenderableWidget(new GuiIconButton(leftPos - 20, topPos + 42, LocalStrings.Convert.getText(), IconManager.getInstance().getPlaceIcon(),
                button -> {
                    final ConvertBagGuiPacket packet = new ConvertBagGuiPacket();
                    ChiselsAndBits.getInstance().getNetworkChannel().sendToServer(packet);
                    packet.execute(Minecraft.getInstance().player);
                },
                Tooltip.create(LocalStrings.Convert.getText())
                ));
    }

    BagContainer getBagContainer() {
        return menu;
    }

    @Override
    protected boolean hasClickedOutside(final double mouseX, final double mouseY, final int guiLeftIn, final int guiTopIn, final int mouseButton) {
        final boolean doThrow = !dontThrow;
        if (requireConfirm && dontThrow)
            dontThrow = false;
        return doThrow && super.hasClickedOutside(mouseX, mouseY, guiLeftIn, guiTopIn, mouseButton);
    }

    @Override
    public void render(
            final @NotNull GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        if (trashBtn.isMouseOver(mouseX, mouseY)) {
            if (isValidBitItem()) {
                final Component msgNotConfirm = !getInHandItem().isEmpty() ? LocalStrings.TrashItem.getText(getInHandItem().getHoverName().getString()) : LocalStrings.Trash.getText();
                final Component msgConfirm = !getInHandItem().isEmpty() ? LocalStrings.ReallyTrashItem.getText(getInHandItem().getHoverName().getString()) : LocalStrings.ReallyTrash.getText();

                this.trashBtn.setTooltip(Tooltip.create(requireConfirm ? msgNotConfirm : msgConfirm));
            } else {
                this.trashBtn.setTooltip(Tooltip.create(LocalStrings.TrashInvalidItem.getText(getInHandItem().getHoverName().getString())));
            }
        }

        this.getBagContainer().bitSlots.forEach(slot -> slot.setActive(false));
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.getBagContainer().bitSlots.forEach(slot -> slot.setActive(true));
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void renderBg(
            final @NotNull GuiGraphics guiGraphics,
            final float partialTicks,
            final int mouseX,
            final int mouseY) {
        final int xOffset = (width - imageWidth) / 2;
        final int yOffset = (height - imageHeight) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BAG_GUI_TEXTURE);

        guiGraphics.blit(BAG_GUI_TEXTURE, xOffset, yOffset, 0,0, imageWidth, imageHeight);

        Matrix4fStack posestack = RenderSystem.getModelViewStack();
        posestack.pushMatrix();
        posestack.translate(this.leftPos, this.topPos, 0.0f);
        RenderSystem.applyModelViewMatrix();

        hoveredBitSlot = null;
        guiGraphics.pose().pushPose();

        for (int slotIdx = 0; slotIdx < getBagContainer().bitSlots.size(); ++slotIdx) {
            final BitSlot slot = getBagContainer().bitSlots.get(slotIdx);
            slot.setActive(true);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);

            final int count = slot.getItem().getCount();
            slot.set(slot.getItem().copyWithCount(1));
            renderSlot(guiGraphics, slot);
            slot.set(slot.getItem().copyWithCount(count));

            if (count != 0) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);

                String s = String.valueOf(count);
                guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
                guiGraphics.pose().scale(0.5f, 0.5f, 1);
                guiGraphics.drawString(font, s, (slot.x + 19 - 3) * 2 - font.width(s), (slot.y + 9 + 3) * 2, 16777215, true);

                guiGraphics.pose().popPose();
            }


            if (isHovering(slot, mouseX, mouseY) && slot.isActive()) {
                final int xDisplayPos = slot.x;
                final int yDisplayPos = slot.y;
                hoveredBitSlot = slot;

                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                final int INNER_SLOT_SIZE = 16;
                guiGraphics.fillGradient(xDisplayPos, yDisplayPos, xDisplayPos + INNER_SLOT_SIZE, yDisplayPos + INNER_SLOT_SIZE, -2130706433, -2130706433);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }

            slot.setActive(false);
        }

        posestack.popMatrix();
        RenderSystem.applyModelViewMatrix();
        guiGraphics.pose().popPose();

        if (!trashBtn.isMouseOver(mouseX, mouseY)) {
            requireConfirm = true;
        }
    }

    private ItemStack getInHandItem() {
        return Minecraft.getInstance().player == null ? ItemStack.EMPTY : Minecraft.getInstance().player.containerMenu.getCarried();
    }

    private boolean isValidBitItem() {
        return getInHandItem().isEmpty() || getInHandItem().getItem() == ModItems.ITEM_BLOCK_BIT.get();
    }

    @Override
    protected void renderLabels(final @NotNull GuiGraphics graphics, final int x, final int y) {
        graphics.drawString(font, Language.getInstance().getVisualOrder(ModItems.ITEM_BIT_BAG_DEFAULT.get().getName(ItemStack.EMPTY)), 8, 6, 0x404040);
        graphics.drawString(font, I18n.get("container.inventory"), 8, imageHeight - 93, 0x404040);
    }
}
