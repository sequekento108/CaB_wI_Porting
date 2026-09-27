package mod.chiselsandbits.forge.data.tag;

import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.registrars.ModItems;
import mod.chiselsandbits.registrars.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class ModItemTagGenerator extends ItemTagsProvider
{

    public ModItemTagGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> holderProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, holderProvider, blockTagsProvider, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(ModTags.Items.BIT_BAG).add(ModItems.ITEM_BIT_BAG_DEFAULT.get(), ModItems.ITEM_BIT_BAG_DYED.get());
        this.tag(ModTags.Items.CHISEL).add(
                ModItems.ITEM_CHISEL_STONE.get(),
                ModItems.ITEM_CHISEL_IRON.get(),
                ModItems.ITEM_CHISEL_GOLD.get(),
                ModItems.ITEM_CHISEL_DIAMOND.get(),
                ModItems.ITEM_CHISEL_NETHERITE.get()
        );
        this.tag(ModTags.Items.FORGE_PAPER).add(Items.PAPER);
        this.tag(ItemTags.PIGLIN_LOVED).add(ModItems.ITEM_CHISEL_GOLD.get());

        this.tag(ItemTags.DURABILITY_ENCHANTABLE).addTags(ModTags.Items.CHISEL);
    }
}
