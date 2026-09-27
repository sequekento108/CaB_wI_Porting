package mod.chiselsandbits.registrars;

import com.communi.suggestu.scena.core.client.models.data.IModelDataKey;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public final class ModModelProperties
{
    private static final Logger                                     LOGGER                       = LogManager.getLogger();
    public static IModelDataKey<BakedModel> UNKNOWN_LAYER_MODEL_PROPERTY = IModelDataKey.create();
    public static IModelDataKey<Map<RenderType, BakedModel>> KNOWN_LAYER_MODEL_PROPERTY   = IModelDataKey.create();

    private ModModelProperties()
    {
        throw new IllegalStateException("Can not instantiate an instance of: ModModelProperties. This is a utility class");
    }

    public static void onModConstruction()
    {
        LOGGER.info("Loaded model property configuration.");
    }

}
