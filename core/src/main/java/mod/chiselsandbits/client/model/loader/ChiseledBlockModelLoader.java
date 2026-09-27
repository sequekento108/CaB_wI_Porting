package mod.chiselsandbits.client.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecificationLoader;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mod.chiselsandbits.client.model.ChiseledBlockModel;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

public final class ChiseledBlockModelLoader implements IModelSpecificationLoader<ChiseledBlockModel>
{

    private static final ChiseledBlockModelLoader INSTANCE = new ChiseledBlockModelLoader();

    public static ChiseledBlockModelLoader getInstance()
    {
        return INSTANCE;
    }

    private ChiseledBlockModelLoader()
    {
    }

    @Override
    public void onResourceManagerReload(@NotNull final ResourceManager resourceManager)
    {
    }

    @Override
    public @NotNull ChiseledBlockModel read(@NotNull final JsonDeserializationContext deserializationContext, @NotNull final JsonObject modelContents)
    {
        return new ChiseledBlockModel();
    }
}
