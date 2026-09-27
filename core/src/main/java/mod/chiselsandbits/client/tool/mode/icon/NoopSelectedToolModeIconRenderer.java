package mod.chiselsandbits.client.tool.mode.icon;

import mod.chiselsandbits.api.client.tool.mode.icon.ISelectedToolModeIconRenderer;
import mod.chiselsandbits.api.util.constants.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class NoopSelectedToolModeIconRenderer implements ISelectedToolModeIconRenderer
{
    static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "noop");

    @Override
    public ResourceLocation getId()
    {
        return ID;
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final ItemStack stack)
    {
        //Noop
    }
}
