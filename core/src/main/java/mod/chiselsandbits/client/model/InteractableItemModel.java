package mod.chiselsandbits.client.model;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecification;
import com.communi.suggestu.scena.core.client.models.loaders.context.IModelBakingContext;
import mod.chiselsandbits.client.model.baked.interactable.InteractableBakedItemModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

import static net.minecraft.client.resources.model.ModelBakery.GENERATION_MARKER;

public class InteractableItemModel implements IModelSpecification<InteractableItemModel>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();

    private ResourceLocation innerModelLocation;
    private UnbakedModel innerModel;

    public InteractableItemModel(final ResourceLocation innerModelLocation)
    {
        this.innerModelLocation = innerModelLocation;
    }



    @Override
    public BakedModel bake(IModelBakingContext iModelBakingContext, ModelBaker modelBaker, Function<Material, TextureAtlasSprite> function, ModelState modelState) {
        this.innerModel = modelBaker.getModel(this.innerModelLocation);

        if (this.innerModel instanceof BlockModel blockModel) {
            if (blockModel.getRootModel() == GENERATION_MARKER) {
                return new InteractableBakedItemModel(ITEM_MODEL_GENERATOR.generateBlockModel(function, blockModel).bake(modelBaker, blockModel, function, modelState, false));
            }
        }

        final BakedModel innerBakedModel = this.innerModel.bake(
                modelBaker,
                function,
                modelState
        );

        return new InteractableBakedItemModel(innerBakedModel);
    }
}
