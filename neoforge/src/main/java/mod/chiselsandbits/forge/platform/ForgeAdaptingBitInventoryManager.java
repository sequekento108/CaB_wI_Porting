package mod.chiselsandbits.forge.platform;

import mod.chiselsandbits.api.inventory.bit.IAdaptingBitInventoryManager;
import mod.chiselsandbits.forge.inventory.bit.IItemHandlerBitInventory;
import mod.chiselsandbits.forge.inventory.bit.IModifiableItemHandlerBitInventory;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.Optional;

public final class ForgeAdaptingBitInventoryManager implements IAdaptingBitInventoryManager {
    private static final ForgeAdaptingBitInventoryManager INSTANCE = new ForgeAdaptingBitInventoryManager();

    public static ForgeAdaptingBitInventoryManager getInstance() {
        return INSTANCE;
    }
    private ForgeAdaptingBitInventoryManager() {
    }

    @Override
    public Optional<Object> create(Object target) {
        return Optional.of(target)
                .filter(IItemHandler.class::isInstance)
                .map(IItemHandler.class::cast)
                .map(itemHandler -> {
                    if (itemHandler instanceof IItemHandlerModifiable)
                        return new IModifiableItemHandlerBitInventory((IItemHandlerModifiable) itemHandler);

                    return new IItemHandlerBitInventory(itemHandler);
                });
    }


}
