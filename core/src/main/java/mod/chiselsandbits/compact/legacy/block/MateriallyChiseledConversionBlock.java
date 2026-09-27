package mod.chiselsandbits.compact.legacy.block;

import mod.chiselsandbits.block.entities.ChiseledBlockEntity;
import mod.chiselsandbits.compact.legacy.block.entity.MateriallyChiseledConversionBlockEntity;
import mod.chiselsandbits.registrars.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class MateriallyChiseledConversionBlock extends Block implements EntityBlock {

    public MateriallyChiseledConversionBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state)
    {
        return new MateriallyChiseledConversionBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return (pLevel1, pPos, pState1, pBlockEntity) -> {
            final BlockEntity blockEntity = pLevel1.getBlockEntity(pPos);
            if (blockEntity instanceof MateriallyChiseledConversionBlockEntity materiallyChiseledConversionBlockEntity) {
                pLevel1.setBlock(pPos, ModBlocks.CHISELED_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_ALL_IMMEDIATE);

                final BlockEntity newEntity = pLevel1.getBlockEntity(pPos);
                if (newEntity instanceof ChiseledBlockEntity chiseledBlockEntity) {
                    chiseledBlockEntity.loadWithComponents(materiallyChiseledConversionBlockEntity.getTag(), pLevel.registryAccess());
                }
            }
        };
    }

}