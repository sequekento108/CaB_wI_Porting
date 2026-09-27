package mod.chiselsandbits.client.registrars;

import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import mod.chiselsandbits.registrars.ModBlocks;
import net.minecraft.client.renderer.RenderType;

public final class ItemBlockRenderTypes
{

    private ItemBlockRenderTypes()
    {
        throw new IllegalStateException("Can not instantiate an instance of: FallbackRenderTypes. This is a utility class");
    }

    public static void onClientConstruction() {
        IRenderTypeManager.getInstance().registerBlockFallbackRenderTypes(registrar -> {
            registrar.register(ModBlocks.CHISELED_BLOCK.get(), RenderType.translucent());
            registrar.register(ModBlocks.BIT_STORAGE.get(), RenderType.cutoutMipped());
            registrar.register(ModBlocks.CHISELED_PRINTER.get(), RenderType.cutoutMipped());
        });
    }
}
