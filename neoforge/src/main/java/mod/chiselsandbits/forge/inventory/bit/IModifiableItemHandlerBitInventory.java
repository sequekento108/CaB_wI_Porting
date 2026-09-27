package mod.chiselsandbits.forge.inventory.bit;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class IModifiableItemHandlerBitInventory extends IItemHandlerBitInventory
{

    private final IItemHandlerModifiable itemHandlerModifiable;

    public IModifiableItemHandlerBitInventory(final IItemHandlerModifiable itemHandler)
    {
        super(itemHandler);
        this.itemHandlerModifiable = itemHandler;
    }

    /**
     * Sets the slot with the given index with the given stack.
     *
     * @param index The index of the slot.
     * @param stack The stack to insert.
     */
    @Override
    protected void setSlotContents(final int index, final ItemStack stack)
    {
        itemHandlerModifiable.setStackInSlot(index, stack);
    }
}
