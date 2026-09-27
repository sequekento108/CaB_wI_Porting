package mod.chiselsandbits.client;

import mod.chiselsandbits.api.plugin.IChiselsAndBitsPlugin;
import mod.chiselsandbits.api.plugin.IPluginManager;
import mod.chiselsandbits.client.registrars.*;
import mod.chiselsandbits.keys.KeyBindingManager;

public class ChiselsAndBitsClient {

    public ChiselsAndBitsClient() {
        BlockEntityRenderers.onClientConstruction();
        BlockEntityWithoutLevelRenderers.onClientConstruction();
        ItemColors.onClientConstruction();
        BlockColors.onClientConstruction();
        ModelLoaders.onClientConstruction();
        ItemBlockRenderTypes.onClientConstruction();
        EventHandlers.onClientConstruction();
        KeyBindingManager.getInstance().onModInitialization();
        Screens.onClientConstruction();
        ItemProperties.onClientConstruction();
        GPUResources.onClientConstruction();
        ClientTooltipComponents.onClientConstruction();

        IPluginManager.getInstance().run(IChiselsAndBitsPlugin::onClientConstruction);
    }
}
