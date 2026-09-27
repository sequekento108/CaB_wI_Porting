package mod.chiselsandbits.fabric.plugin;

import mod.chiselsandbits.api.plugin.IPluginDiscoverer;
import mod.chiselsandbits.api.plugin.PluginData;
import mod.chiselsandbits.fabric.plugin.asm.FabricPluginDiscoverer;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Function;

public final class FabricPluginManager implements IPluginDiscoverer
{
    private static final FabricPluginManager INSTANCE = new FabricPluginManager();

    public static FabricPluginManager getInstance()
    {
        return INSTANCE;
    }

    private FabricPluginManager()
    {
    }

    @Override
    public <A, I extends Annotation, T> Collection<PluginData<T>> loadPlugins(
      final Class<A> annotationType, final Class<I> instanceAnnotationType, final Class<T> pluginSpecificationType, final Function<T, String> idExtractor)
    {
        final IPluginDiscoverer asmBasedDiscoverer = new FabricPluginDiscoverer();
        return asmBasedDiscoverer.loadPlugins(annotationType, instanceAnnotationType, pluginSpecificationType, idExtractor);
    }
}
