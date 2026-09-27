package mod.chiselsandbits.client.registrars;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.client.model.loader.BitBlockModelLoader;
import mod.chiselsandbits.client.model.loader.ChiseledBlockModelLoader;
import mod.chiselsandbits.client.model.loader.InteractableModelLoader;
import net.minecraft.resources.ResourceLocation;

public final class ModelLoaders {

    private ModelLoaders() {
        throw new IllegalStateException("Can not instantiate an instance of: ModelLoaders. This is a utility class");
    }

    public static void onClientConstruction() {
        IModelManager.getInstance().registerModelLoader(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "chiseled_block"),
                ChiseledBlockModelLoader.getInstance()
        );
        IModelManager.getInstance().registerModelLoader(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "bit"),
                BitBlockModelLoader.getInstance()
        );
        IModelManager.getInstance().registerModelLoader(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "interactable_model"),
                new InteractableModelLoader()
        );
    }
}
