package mod.chiselsandbits.client.model.baked.chiseled;

import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.registries.IPlatformRegistryManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.material.Fluid;

import java.util.BitSet;
import java.util.List;

public class FluidRenderingManager
{
    private static final FluidRenderingManager INSTANCE = new FluidRenderingManager();

    public static FluidRenderingManager getInstance()
    {
        return INSTANCE;
    }

    private boolean renderTypesSetup = false;
    private final BitSet renderTypes = new BitSet(RenderType.chunkBufferLayers().size());

    private FluidRenderingManager()
    {
    }

    private void setupRenderTypes() {
        if (renderTypesSetup)
            return;

        renderTypes.clear();
        final List<RenderType> blockRenderTypes = RenderType.chunkBufferLayers();
        for (int i = 0; i < blockRenderTypes.size(); i++)
        {
            final RenderType renderType = blockRenderTypes.get(i);
            for (final Fluid fluid : IPlatformRegistryManager.getInstance().getFluids().getValues())
            {
                if (IRenderTypeManager.getInstance().canRenderInType(fluid.defaultFluidState(), renderType))
                {
                    renderTypes.set(i);
                    break;
                }
            }
        }

        renderTypesSetup = true;
    }

    public boolean isFluidRenderType(final RenderType renderType) {
        setupRenderTypes();
        return renderTypes.stream()
                .mapToObj(RenderType.chunkBufferLayers()::get)
                .anyMatch(fancyRenderType ->
                        fancyRenderType.equals(renderType) || //Fancy render mode.
                        ChiselRenderType.fromLayer(fancyRenderType, true).has(fancyRenderType) //Fabulous render mode.
                );
    }
}
