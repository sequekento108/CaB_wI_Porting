package mod.chiselsandbits.client.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecificationLoader;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mod.chiselsandbits.client.model.BitBlockModel;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

public final class BitBlockModelLoader implements IModelSpecificationLoader<BitBlockModel>
{
    private static final BitBlockModelLoader INSTANCE = new BitBlockModelLoader();

    public static BitBlockModelLoader getInstance()
    {
        return INSTANCE;
    }

    private BitBlockModelLoader()
    {
    }

    @Override
    public void onResourceManagerReload(@NotNull final ResourceManager resourceManager)
    {
    }

    @NotNull
    @Override
    public BitBlockModel read(@NotNull final JsonDeserializationContext deserializationContext, @NotNull final JsonObject modelContents)
    {
        return new BitBlockModel();
    }
}
