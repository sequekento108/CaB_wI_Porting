package mod.chiselsandbits.client.colors;

import mod.chiselsandbits.api.block.state.id.IBlockStateIdManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChiseledBlockBlockColor implements BlockColor
{
    private static final int TINT_MASK = 0xff;
    private static final int TINT_BITS = 8;

    @Override
    public int getColor(
      @NotNull final BlockState state, @Nullable final BlockAndTintGetter displayReader, @Nullable final BlockPos pos, final int color)
    {
        final BlockState containedState = IBlockStateIdManager.getInstance().getBlockStateFrom(color >> TINT_BITS);
        int tintValue = color & TINT_MASK;
        return Minecraft.getInstance().getBlockColors().getColor(containedState, displayReader, pos, tintValue);
    }
}
