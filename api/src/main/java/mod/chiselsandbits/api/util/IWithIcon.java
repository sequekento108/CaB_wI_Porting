package mod.chiselsandbits.api.util;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * An object with an icon to render.
 */
public interface IWithIcon
{
    /**
     * The icon to render.
     *
     * @return The icon.
     */
    @NotNull
    ResourceLocation getIcon();
}
