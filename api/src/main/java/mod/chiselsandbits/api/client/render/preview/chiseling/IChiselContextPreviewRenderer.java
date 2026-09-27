package mod.chiselsandbits.api.client.render.preview.chiseling;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.chiselsandbits.api.chiseling.IChiselingContext;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer which is used to render the chiseling preview.
 */
public interface IChiselContextPreviewRenderer
{

    /**
     * The id of teh renderer.
     * Used to give the player a selection option for the preview renderer.
     *
     * @return The id of the preview renderer.
     */
    ResourceLocation getId();

    /**
     * Invoked by the engine to render previews of the given {@link IChiselingContext}.
     *
     * @param matrixStack The matrix stack to render into.
     * @param currentContextSnapshot The current snapshot to render.
     */
    void renderExistingContextsBoundingBox(final PoseStack matrixStack, final IChiselingContext currentContextSnapshot);
}
