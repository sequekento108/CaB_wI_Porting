package mod.chiselsandbits.api.util;

import mod.chiselsandbits.api.blockinformation.BlockInformation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingleBlockBlockReader implements BlockGetter
{

    protected final BlockInformation blockInformation;
    protected final Block            blk;
    protected final BlockPos pos;
    protected final BlockGetter source;

    public SingleBlockBlockReader(final BlockInformation blockInformation, final Block blk)
    {
        this.blockInformation = blockInformation;
        this.blk = blk;
        this.pos = BlockPos.ZERO;
        this.source = null;
    }

    public SingleBlockBlockReader(final BlockInformation blockInformation)
    {
        this.blockInformation = blockInformation;
        this.blk = blockInformation.blockState().getBlock();
        this.pos = BlockPos.ZERO;
        this.source = null;
    }

    public SingleBlockBlockReader(final BlockInformation blockInformation, final Block blk, final BlockPos pos)
    {
        this.blockInformation = blockInformation;
        this.blk = blk;
        this.pos = pos;
        this.source = null;
    }

    public SingleBlockBlockReader(final BlockInformation blockInformation, final BlockPos pos)
    {
        this.blockInformation = blockInformation;
        this.blk = blockInformation.blockState().getBlock();
        this.pos = pos;
        this.source = null;
    }

    public SingleBlockBlockReader(final BlockInformation blockInformation, final Block blk, final BlockGetter source)
    {
        this.blockInformation = blockInformation;
        this.blk = blk;
        this.source = source;
        this.pos = BlockPos.ZERO;
    }

    public SingleBlockBlockReader(final BlockInformation blockInformation, final BlockGetter source)
    {
        this.blockInformation = blockInformation;
        this.blk = blockInformation.blockState().getBlock();
        this.source = source;
        this.pos = BlockPos.ZERO;
    }

    public SingleBlockBlockReader(final BlockInformation blockInformation, final Block blk, final BlockPos pos, final BlockGetter source)
    {
        this.blockInformation = blockInformation;
        this.blk = blk;
        this.pos = pos;
        this.source = source;
    }

    public SingleBlockBlockReader(final BlockInformation blockInformation, final BlockPos pos, final BlockGetter source)
    {
        this.blockInformation = blockInformation;
        this.blk = blockInformation.blockState().getBlock();
        this.pos = pos;
        this.source = source;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(@NotNull final BlockPos pos)
    {
        if (pos == this.pos && blk instanceof EntityBlock)
        {
            return ((EntityBlock) blk).newBlockEntity(this.pos, blockInformation.blockState());
        }

        return source == null ? null : source.getBlockEntity(pos);
    }

    @NotNull
    @Override
    public BlockState getBlockState(@NotNull final BlockPos pos)
    {
        if (pos == this.pos)
        {
            return blockInformation.blockState();
        }

        return source == null ? Blocks.AIR.defaultBlockState() : source.getBlockState(pos);
    }

    @NotNull
    @Override
    public FluidState getFluidState(@NotNull final BlockPos pos)
    {
        return getBlockState(pos).getFluidState();
    }

    @Override
    public int getHeight()
    {
        return 0;
    }

    @Override
    public int getMinBuildHeight()
    {
        return 0;
    }
}
