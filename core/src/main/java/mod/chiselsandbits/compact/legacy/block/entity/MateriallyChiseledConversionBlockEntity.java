package mod.chiselsandbits.compact.legacy.block.entity;

import mod.chiselsandbits.registrars.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class MateriallyChiseledConversionBlockEntity extends BlockEntity {

    private CompoundTag tag = new CompoundTag();

    public MateriallyChiseledConversionBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityTypes.MATERIAL_CHISELED_CONVERSION.get(), pPos, pBlockState);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        this.tag = tag;
    }

    public CompoundTag getTag() {
        return tag;
    }
}
