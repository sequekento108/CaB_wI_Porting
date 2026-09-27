package mod.chiselsandbits.plugin;

import com.google.common.collect.ImmutableSet;
import mod.chiselsandbits.api.plugin.*;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class PluginManger implements IPluginManager {
    private static final PluginManger INSTANCE = new PluginManger();

    public static PluginManger getInstance() {
        return INSTANCE;
    }

    private ImmutableSet<PluginData<IChiselsAndBitsPlugin>> pluginDatas = ImmutableSet.of();
    private ImmutableSet<IChiselsAndBitsPlugin> plugins = ImmutableSet.of();

    private PluginManger() {
    }

    @Override
    public ImmutableSet<IChiselsAndBitsPlugin> getPlugins() {
        return plugins;
    }

    @Override
    public void run(Consumer<IChiselsAndBitsPlugin> callback) {
        for (PluginData<IChiselsAndBitsPlugin> pluginData : pluginDatas) {
            callback.accept(pluginData.plugin());
        }
    }

    public void detect() {
        this.pluginDatas = ImmutableSet.copyOf(IPluginDiscoverer.getInstance().loadPlugins(
                ChiselsAndBitsPlugin.class,
                ChiselsAndBitsPlugin.Instance.class,
                IChiselsAndBitsPlugin.class,
                IChiselsAndBitsPlugin::getId
        ));
        this.plugins = ImmutableSet.copyOf(this.pluginDatas.stream().map(PluginData::plugin).collect(Collectors.toSet()));
    }
}
