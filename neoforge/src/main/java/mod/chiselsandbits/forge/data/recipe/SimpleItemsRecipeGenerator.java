package mod.chiselsandbits.forge.data.recipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.forge.utils.CollectorUtils;
import mod.chiselsandbits.registrars.ModItems;
import mod.chiselsandbits.registrars.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
public class SimpleItemsRecipeGenerator extends AbstractRecipeGenerator {
    private final boolean shapeless;
    private final List<String> pattern;
    private final Map<Character, TagKey<Item>> tagMap;
    private final Map<Character, ItemLike> itemMap;

    public SimpleItemsRecipeGenerator(
            final PackOutput generator,
            final ItemLike itemProvider,
            final String pattern,
            final Map<Character, TagKey<Item>> tagMap, final Map<Character, ItemLike> itemMap,
            final CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, itemProvider, registries);
        this.shapeless = false;
        this.pattern = Arrays.asList(pattern.split(";"));
        this.tagMap = tagMap;
        this.itemMap = itemMap;
    }

    public SimpleItemsRecipeGenerator(
            final PackOutput generator,
            final ItemLike itemProvider,
            final List<TagKey<Item>> tagMap,
            final List<ItemLike> itemMap,
            final CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, itemProvider, registries);
        this.shapeless = true;
        this.pattern = ImmutableList.of("   ", "   ", "   ");
        this.tagMap = tagMap.stream().collect(CollectorUtils.toEnumeratedCharacterKeyedMap());
        this.itemMap = itemMap.stream().collect(CollectorUtils.toEnumeratedCharacterKeyedMap());
    }

    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event) {
        event.getGenerator().addProvider(true,
                new SimpleItemsRecipeGenerator(
                        event.getGenerator().getPackOutput(),
                        ModItems.ITEM_BIT_BAG_DEFAULT.get(),
                        "www;wbw;www",
                        ImmutableMap.of(
                                'w', ItemTags.WOOL
                        ),
                        ImmutableMap.of(
                                'b', ModItems.ITEM_BLOCK_BIT.get()
                        ),
                        event.getLookupProvider()
                )
        );

        event.getGenerator().addProvider(true,
                new SimpleItemsRecipeGenerator(
                        event.getGenerator().getPackOutput(),
                        ModItems.MAGNIFYING_GLASS.get(),
                        "cg ;s  ;   ",
                        ImmutableMap.of(
                                'c', ModTags.Items.CHISEL,
                                'g', Tags.Items.GLASS_BLOCKS,
                                's', Tags.Items.RODS_WOODEN
                        ),
                        ImmutableMap.of(),
                        event.getLookupProvider()
                )
        );

        event.getGenerator().addProvider(true,
                new SimpleItemsRecipeGenerator(
                        event.getGenerator().getPackOutput(),
                        ModItems.MEASURING_TAPE.get(),
                        "  s;isy;ii ",
                        ImmutableMap.of(
                                'i', Tags.Items.INGOTS_IRON,
                                's', Tags.Items.STRINGS,
                                'y', Tags.Items.DYES_YELLOW
                        ),
                        ImmutableMap.of(),
                        event.getLookupProvider()
                )
        );

        event.getGenerator().addProvider(true,
                new SimpleItemsRecipeGenerator(
                        event.getGenerator().getPackOutput(),
                        ModItems.QUILL.get(),
                        ImmutableList.of(
                                Tags.Items.FEATHERS,
                                Tags.Items.DYES_BLACK,
                                Tags.Items.DYES_YELLOW
                        ),
                        ImmutableList.of(),
                        event.getLookupProvider()
                )
        );

        event.getGenerator().addProvider(true,
                new SimpleItemsRecipeGenerator(
                        event.getGenerator().getPackOutput(),
                        ModItems.SEALANT_ITEM.get(),
                        ImmutableList.of(
                                Tags.Items.SLIMEBALLS
                        ),
                        ImmutableList.of(
                                Items.HONEY_BOTTLE
                        ),
                        event.getLookupProvider()
                )
        );

        event.getGenerator().addProvider(true,
                new SimpleItemsRecipeGenerator(
                        event.getGenerator().getPackOutput(),
                        ModItems.WRENCH.get(),
                        " pb; pp;p  ",
                        ImmutableMap.of(
                                'p', ItemTags.PLANKS
                        ),
                        ImmutableMap.of(
                                'b', ModItems.ITEM_BLOCK_BIT.get()
                        ),
                        event.getLookupProvider()
                )
        );

        event.getGenerator().addProvider(true,
                new SimpleItemsRecipeGenerator(
                        event.getGenerator().getPackOutput(),
                        ModItems.UNSEAL_ITEM.get(),
                        ImmutableList.of(),
                        ImmutableList.of(
                                Blocks.WET_SPONGE
                        ),
                        event.getLookupProvider()
                )
        );
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput writer) {
        if (this.shapeless) {
            final ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, getItemProvider());
            tagMap.forEach((ingredientKey, tag) -> {
                builder.requires(tag);
                builder.unlockedBy("has_tag_" + ingredientKey, has(tag));
            });
            itemMap.forEach((ingredientKey, item) -> {
                builder.requires(item);
                builder.unlockedBy("has_item_" + ingredientKey, has(item));
            });
            builder.save(writer);
        } else {
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
}
