package mod.chiselsandbits.forge.data.tag;

import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.registrars.ModBlocks;
import mod.chiselsandbits.registrars.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider
{


    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(ModTags.Blocks.BLOCKED_CHISELABLE);
        this.tag(ModTags.Blocks.FORCED_CHISELABLE)
                .addTag(BlockTags.LEAVES)
                .add(Blocks.GRASS_BLOCK)
                .add(Blocks.ICE)
                .add(Blocks.PACKED_ICE)
                .add(Blocks.PACKED_ICE);
        this.tag(ModTags.Blocks.CHISELED_BLOCK).add(
                ModBlocks.CHISELED_BLOCK.get()
        );

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.CHISELED_PRINTER.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.MODIFICATION_TABLE.get())
                .add(ModBlocks.BIT_STORAGE.get())
                .add(ModBlocks.PATTERN_SCANNER.get());

    }

    @Override
    public String getName() {
        return "Chisels & Bits Block Tags";
    }
}
