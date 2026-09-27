package mod.chiselsandbits.forge.compat.create;

import mod.chiselsandbits.api.multistate.accessor.identifier.IAreaShapeIdentifier;
import net.minecraft.client.renderer.RenderType;

public record ChiseledBlockModelKey(IAreaShapeIdentifier identifier, RenderType type) {
}
