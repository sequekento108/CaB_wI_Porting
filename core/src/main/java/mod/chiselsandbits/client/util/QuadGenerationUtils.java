package mod.chiselsandbits.client.util;

import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.client.model.baked.face.FaceManager;
import mod.chiselsandbits.client.model.baked.face.model.BakedQuadAdapter;
import mod.chiselsandbits.client.model.baked.face.model.ModelQuadLayer;
import mod.chiselsandbits.client.model.baked.face.model.VertexData;
import mod.chiselsandbits.utils.LightUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class QuadGenerationUtils {

    private QuadGenerationUtils() {
        throw new IllegalStateException("Tried to instantiate: 'QuadGenerationUtils', but this is a utility class.");
    }

    public static void generateQuads(List<BakedQuad> target, long primaryStateRenderSeed, @NotNull RenderType renderType, BlockInformation blockInformation, Direction cullDirection, Vector3f from, Vector3f to, BiFunction<ModelQuadLayer, BakedQuad, BakedQuad> resultAdapter) {
        final Collection<ModelQuadLayer> quadLayers = FaceManager.getInstance().getCachedLayersFor(blockInformation, cullDirection, renderType, primaryStateRenderSeed, renderType);

        if (quadLayers != null) {
            for (final ModelQuadLayer layer : quadLayers) {

                final Collection<VertexData> adaptedVertices;
                try {
                    adaptedVertices = VertexDataUtils.adaptVertices(layer.vertexData(), cullDirection, from, to);
                } catch (IllegalStateException e) {
                    continue;
                }

                final BakedQuadAdapter adapter = new BakedQuadAdapter(adaptedVertices, layer.color());
                LightUtil.put(adapter, layer.sourceQuad());
                adapter.setQuadTint(layer.tint());
                adapter.setApplyDiffuseLighting(layer.shade());
                adapter.setTexture(layer.sprite());
                adapter.setQuadOrientation(cullDirection);
                final BakedQuad quad = adapter.build();

                target.add(resultAdapter.apply(layer, quad));
            }
        }
    }
}
