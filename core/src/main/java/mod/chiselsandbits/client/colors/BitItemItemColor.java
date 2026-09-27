package mod.chiselsandbits.client.colors;

import com.communi.suggestu.scena.core.client.fluid.IClientFluidManager;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.chiseling.eligibility.IEligibilityManager;
import mod.chiselsandbits.item.bit.BitItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;

public class BitItemItemColor implements ItemColor
{
    private static final int TINT_MASK = 0xff;

    @Override
    public int getColor(
      final ItemStack stack,
      final int tint)
    {
        if (!(stack.getItem() instanceof BitItem))
            return 0xffffff;

        final BlockInformation blockInformation = ((BitItem) stack.getItem()).getBlockInformation(stack);
        if(blockInformation.blockState().getBlock() instanceof LiquidBlock) {
            if ((!Minecraft.getInstance().options.keyShift.isUnbound() && Minecraft.getInstance().options.keyShift.isDown()) || (Minecraft.getInstance().getWindow() != null && Screen.hasShiftDown())) {
                return -1; //No coloring on liquids when pressing shifts -> Buckets
            }

            return IClientFluidManager.getInstance().getFluidColor(new FluidInformation(
                    blockInformation.blockState().getFluidState().getType(),
                    1
            ));
        }

        if ((!Minecraft.getInstance().options.keyShift.isUnbound() && Minecraft.getInstance().options.keyShift.isDown()) || (Minecraft.getInstance().getWindow() != null && Screen.hasShiftDown()))
        {
            final Block block = blockInformation.blockState().getBlock();
            final Item item = block.asItem();
            int tintValue = tint & TINT_MASK;

            if (item != Items.AIR)
            {
                return Minecraft.getInstance().itemColors.getColor(new ItemStack(item, 1), tintValue);
            }

            return 0xffffff;
        }

        if (!IEligibilityManager.getInstance().canBeChiseled(blockInformation))
        {
            return 0xffffff;
        }

        final ItemStack workingStack = new ItemStack(blockInformation.blockState().getBlock(), 1);
        if (workingStack.getItem() instanceof AirItem)
            return 0xffffff;

        return Minecraft.getInstance().itemColors.getColor(workingStack, tint);
    }
}
