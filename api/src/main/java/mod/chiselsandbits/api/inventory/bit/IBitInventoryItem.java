package mod.chiselsandbits.api.inventory.bit;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

/**
 * Represents an item which is a bit inventory.
 */
public interface IBitInventoryItem
{

    /**
     * Creates a bit inventory which is represented by the
     * given itemstack which contains this item.
     *
     * @param stack The stack to create an inventory of.
     *
     * @return The bit inventory.
     */
    IBitInventoryItemStack create(final ItemStack stack);
}
