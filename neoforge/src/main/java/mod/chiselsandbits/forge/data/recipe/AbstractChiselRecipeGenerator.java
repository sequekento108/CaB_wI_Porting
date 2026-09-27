package mod.chiselsandbits.forge.data.recipe;

import mod.chiselsandbits.api.item.chisel.IChiselItem;
import mod.chiselsandbits.api.util.ParamValidator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AbstractChiselRecipeGenerator extends AbstractRecipeGenerator
{
    private final TagKey<Item> rodTag;
    private final TagKey<Item> ingredientTag;

    protected AbstractChiselRecipeGenerator(final PackOutput generator, final Item result, TagKey<Item> ingredientTag, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(generator, ParamValidator.isInstanceOf(result, IChiselItem.class), registries);
        this.ingredientTag = ingredientTag;
        this.rodTag = Tags.Items.RODS_WOODEN;
    }

    protected AbstractChiselRecipeGenerator(
      final PackOutput generator,
      final Item result,
      final TagKey<Item> rodTag,
      final TagKey<Item> ingredientTag,
      CompletableFuture<HolderLookup.Provider> registries)
    {
        super(generator, ParamValidator.isInstanceOf(result, IChiselItem.class), registries);
        this.rodTag = rodTag;
        this.ingredientTag = ingredientTag;
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput writer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, getItemProvider())
                .pattern("st")
                .pattern("  ")
                .define('s', rodTag)
                .define('t', ingredientTag)
                .unlockedBy("has_rod", has(rodTag))
                .unlockedBy("has_ingredient", has(ingredientTag))
                .save(writer);
    }
}