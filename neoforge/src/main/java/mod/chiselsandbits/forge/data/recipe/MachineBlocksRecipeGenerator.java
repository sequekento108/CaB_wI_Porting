package mod.chiselsandbits.forge.data.recipe;

import com.google.common.collect.ImmutableMap;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.registrars.ModBlocks;
import mod.chiselsandbits.registrars.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class MachineBlocksRecipeGenerator extends AbstractRecipeGenerator {
    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event) {
        event.getGenerator().addProvider(true,
                new MachineBlocksRecipeGenerator(
                        event.getGenerator().getPackOutput(),
                        ModBlocks.CHISELED_PRINTER.get(),
                        " c ;l l;sss",
                        ImmutableMap.of(
                                'c', ModTags.Items.CHISEL,
                                'l', ItemTags.LOGS
                        ),
                        ImmutableMap.of(
                                's', Blocks.SMOOTH_STONE_SLAB
                        ),
                        event.getLookupProvider()
                )
        );

        event.getGenerator().addProvider(true,
                new MachineBlocksRecipeGenerator(
                        event.getGenerator().getPackOutput(),
                        ModBlocks.MODIFICATION_TABLE.get(),
                        "scs;nbn;ppp",
                        ImmutableMap.of(
                                's', ItemTags.WOODEN_SLABS,
                                'n', Tags.Items.NUGGETS_IRON,
                                'b', ItemTags.LOGS,
                                'p', ItemTags.PLANKS,
                                'c', ModTags.Items.CHISEL
                        ),
                        ImmutableMap.of(),
                        event.getLookupProvider()
                )
        );

        event.getGenerator().addProvider(true,
                new MachineBlocksRecipeGenerator(
                        event.getGenerator().getPackOutput(),
                        ModBlocks.BIT_STORAGE.get(),
                        "igi;glg;ici",
                        ImmutableMap.of(
                                'g', Tags.Items.GLASS_BLOCKS,
                                'l', ItemTags.LOGS,
                                'i', Tags.Items.INGOTS_IRON,
                                'c', ModTags.Items.CHISEL
                        ),
                        ImmutableMap.of(),
                        event.getLookupProvider()
                )
        );
    }

    private final List<String> pattern;
    private final Map<Character, TagKey<Item>> tagMap;
    private final Map<Character, ItemLike> itemMap;

    private MachineBlocksRecipeGenerator(
            final PackOutput generator,
            final ItemLike result,
            final String pattern,
            final Map<Character, TagKey<Item>> tagMap,
            final Map<Character, ItemLike> itemMap,
            final CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, result, registries);
        this.pattern = Arrays.asList(pattern.split(";"));
        this.tagMap = tagMap;
        this.itemMap = itemMap;
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput writer) {
        final ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, getItemProvider());
        pattern.forEach(builder::pattern);
        tagMap.forEach((ingredientKey, tag) -> {
            builder.define(ingredientKey, tag);
            builder.unlockedBy("has_" + ingredientKey, has(tag));
        });
        itemMap.forEach((ingredientKey, item) -> {
            builder.define(ingredientKey, item);
            builder.unlockedBy("has_" + ingredientKey, has(item));
        });
        builder.save(writer);
    }
}
