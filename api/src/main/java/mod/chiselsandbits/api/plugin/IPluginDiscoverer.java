package mod.chiselsandbits.api.plugin;

import mod.chiselsandbits.api.IChiselsAndBitsAPI;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Function;

/**
 * The platform plugin manager which can load the plugins for C{@literal &}B on a given platform.
 */
public interface IPluginDiscoverer
{

    /**
     * Gives access to the platform's plugin manager.
     *
     * @return The platform's plugin manager.
     */
    static IPluginDiscoverer getInstance() {
        return IChiselsAndBitsAPI.getInstance().getPluginDiscoverer();
    }

    /**
     * Loads the plugins of a given type, potentially marked with a given annotation type.
     *
     * @param annotationType The annotation type to look for.
     * @param instanceAnnotationType The annotation type that marks the instance of the plugin.
     * @param pluginSpecificationType The plugin specification interface type to look for.
     * @param idExtractor The function to extract the id from the plugin specification.
     * @param <A> The type of the annotation.
     * @param <I> The type of the instance annotation.
     * @param <T> The type of the interface.
     * @return All loaded plugins available.
     */
    <A, I extends Annotation, T> Collection<PluginData<T>> loadPlugins(
      final Class<A> annotationType,
      final Class<I> instanceAnnotationType,
      final Class<T> pluginSpecificationType,
      final Function<T, String> idExtractor
    );
}
