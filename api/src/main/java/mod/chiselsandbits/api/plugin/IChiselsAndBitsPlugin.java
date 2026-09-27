package mod.chiselsandbits.api.plugin;

/**
 * Represents a plugin for ChiselsAndBits.
 *
 * Plugins have callbacks that can be invoked by aequivaleo.
 * See their documentation for more information.
 *
 * All methods are potentially invoked in parallel with other plugins, or even aequivaleo itself.
 */
public interface IChiselsAndBitsPlugin
{
    /**
     * The id of the plugin.
     * Has to be unique over all plugins.
     *
     * @return The id.
     */
    String getId();

    /**
     * Invoked when the plugin is constructed.
     */
    default void onConstruction() {};

    /**
     * Called after ChiselsAndBits client construction completes.
     */
    default void onClientConstruction() {};

    /**
     * Invoked by chisels and bits when the platform it runs on (so forge or fabric) indicates that mod initialization should happen.
     */
    default void onInitialize() {};
}
