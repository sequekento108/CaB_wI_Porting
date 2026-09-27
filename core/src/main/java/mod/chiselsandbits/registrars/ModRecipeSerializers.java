package mod.chiselsandbits.registrars;

import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.recipe.BagDyeingRecipe;
import mod.chiselsandbits.recipe.modificationtable.ModificationTableRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ModRecipeSerializers
{
    private static final Logger                                                   LOGGER              = LogManager.getLogger();
    private static final IRegistrar<RecipeSerializer<?>> SERIALIZER_REGISTER = IRegistrar.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);
    public static final IRegistryObject<SimpleCraftingRecipeSerializer<BagDyeingRecipe>> BAG_DYEING          =
      SERIALIZER_REGISTER.register("bag_dyeing", () -> new SimpleCraftingRecipeSerializer<>(BagDyeingRecipe::new));
    public static        IRegistryObject<ModificationTableRecipeSerializer>       MODIFICATION_TABLE  = SERIALIZER_REGISTER
      .register("modification_table", ModificationTableRecipeSerializer::new);

    private ModRecipeSerializers()
    {
        throw new IllegalStateException("Can not instantiate an instance of: ModRecipeSerializers. This is a utility class");
    }

    public static void onModConstruction()
    {
        LOGGER.info("Loaded recipe serializer configuration.");
    }

}
