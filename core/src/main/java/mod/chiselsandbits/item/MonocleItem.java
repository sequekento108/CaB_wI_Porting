package mod.chiselsandbits.item;

import com.communi.suggestu.scena.core.item.IWearableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MonocleItem extends Item implements IWearableItem
{
    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        protected @NotNull ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
            return dispenseArmor(source, stack) ? stack : super.execute(source, stack);
        }
    };

    private static boolean dispenseArmor(BlockSource source, ItemStack stack) {
        BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
        List<LivingEntity> list = source.level().getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmorEntitySelector(stack)));
        if (list.isEmpty()) {
            return false;
        } else {
            LivingEntity targetEntity = list.get(0);
            EquipmentSlot slot = targetEntity.getEquipmentSlotForItem(stack);
            ItemStack stackToDispense = stack.split(1);
            targetEntity.setItemSlot(slot, stackToDispense);
            if (targetEntity instanceof Mob) {
                ((Mob)targetEntity).setDropChance(slot, 2.0F);
                ((Mob)targetEntity).setPersistenceRequired();
            }

            return true;
        }
    }

    public MonocleItem(final Properties itemProperties)
    {
        super(itemProperties);

        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return getSlot();
    }

    public Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GOLD;
    }

    @Override
    public EquipmentSlot getSlot()
    {
        return EquipmentSlot.HEAD;
    }
}
