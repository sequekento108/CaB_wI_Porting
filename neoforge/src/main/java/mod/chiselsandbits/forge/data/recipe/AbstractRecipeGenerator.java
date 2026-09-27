package mod.chiselsandbits.forge.data.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public abstract class AbstractRecipeGenerator extends RecipeProvider
{
    private final ItemLike itemProvider;

    public AbstractRecipeGenerator(PackOutput packOutput, ItemLike itemProvider, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
        this.itemProvider = itemProvider;
    }


    public ItemLike getItemProvider()
    {
        return itemProvider;
    }

    @Override
    public final @NotNull String getName()
    {
        return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(itemProvider.asItem())) + " recipe generator";
    }
}