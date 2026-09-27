package mod.chiselsandbits.api.neighborhood;

import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.multistate.accessor.IAreaAccessor;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Marker interface used to detect block neighborhoods in cache keys.
 */
public interface IBlockNeighborhood
{

    /**
     * Empty neighborhood.
     */
    IBlockNeighborhood EMPTY = new IBlockNeighborhood()
    {
        @Override
        public @NotNull BlockInformation getBlockInformation(final Direction direction)
        {
            return BlockInformation.AIR;
        }

        @Override
        public IAreaAccessor getAreaAccessor(final Direction direction)
        {
            return null;
        }
    };

    /**
     * Returns the blocks neighbor in the given direction.
     * @param direction The direction.
     * @return The blockstate
     */
    @NotNull
    BlockInformation getBlockInformation(final Direction direction);

    /**
     * Returns the blocks potential area accessor neighbor in the given direction.
     * @param direction The direction.
     * @return The area accessor.
     */
    @Nullable
    IAreaAccessor getAreaAccessor(final Direction direction);
}
