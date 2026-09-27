package mod.chiselsandbits.client.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecificationLoader;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mod.chiselsandbits.client.model.InteractableItemModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

public final class InteractableModelLoader implements IModelSpecificationLoader<InteractableItemModel>
{
    @Override
    public void onResourceManagerReload(@NotNull final ResourceManager resourceManager)
    {
        //The models clean up their own inner caches, since they are not static.
    }

    @NotNull
    @Override
    public InteractableItemModel read(@NotNull final JsonDeserializationContext deserializationContext, final JsonObject modelContents)
    {
        final String parent = modelContents.get("parent").getAsString();
        final ResourceLocation parentLocation = ResourceLocation.parse(parent);

        return new InteractableItemModel(parentLocation);
    }
}

