package mod.chiselsandbits.compact.legacy;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Deprecated
public class MateriallyChiseledConversionItem extends Item {

    public MateriallyChiseledConversionItem(Properties $$0) {
        super($$0);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int inventorySlot, boolean isCursor) {
        if (!(entity instanceof Player player))
            return;

        player.getInventory().removeItem(stack);
        stack = UpgradeUtils.UpgradeChiseledBlockItemStack(stack, level.registryAccess());
        player.getInventory().add(inventorySlot,  stack);
    }
}
