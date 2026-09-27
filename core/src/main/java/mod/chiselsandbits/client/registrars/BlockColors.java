package mod.chiselsandbits.client.registrars;

import com.communi.suggestu.scena.core.client.rendering.IColorManager;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import mod.chiselsandbits.client.colors.ChiseledBlockBlockColor;
import mod.chiselsandbits.registrars.ModBlocks;
import net.minecraft.world.level.block.Block;

public final class BlockColors
{

    private BlockColors()
    {
        throw new IllegalStateException("Can not instantiate an instance of: BlockColors. This is a utility class");
    }

    public static void onClientConstruction()
    {
        IColorManager.getInstance().setupBlockColors(
          configuration -> {
              configuration.register(
                      new ChiseledBlockBlockColor(),
                      ModBlocks.CHISELED_BLOCK.get()
              );
          }
        );
    }
}
