package mod.chiselsandbits.utils;

import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.minecraft.world.level.block.Block;

import java.util.Collection;

public final class RegistryObjectUtils {

    private RegistryObjectUtils() {
        throw new IllegalStateException("Can not instantiate an instance of: RegistryObjectUtils. This is a utility class");
    }

    public static Block[] toArray(final Collection<IRegistryObject<Block>> blockRegistrars) {
        return blockRegistrars.stream().map(IRegistryObject::get).toArray(Block[]::new);
    }
}
