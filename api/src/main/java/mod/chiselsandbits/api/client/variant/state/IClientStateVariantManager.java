package mod.chiselsandbits.api.client.variant.state;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import mod.chiselsandbits.api.IChiselsAndBitsAPI;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Supplier;

/**
 * The state variant provider for the client.
 * Handles client specific logic related to the state variant.
 */
public interface IClientStateVariantManager {

    static IClientStateVariantManager getInstance() {
        return IChiselsAndBitsAPI.getInstance().getClientStateVariantManager();
    }

    /**
     * Registers a new state variant provider.
     *
     * @param provider The provider to register.
     * @return The manager instance.
     */
    IClientStateVariantManager registerStateVariantProvider(Supplier<Block> block, IClientStateVariantProvider provider);

    /**
     * Retrieves the block model data for the state block information.
     *
     * @param blockInformation The state block information.
     * @return The block model data.
     */
    IBlockModelData getBlockModelData(BlockInformation blockInformation);

    /**
     * Invoked to append a tooltip for the given variant in the block information.
     * If no variant is found this method does nothing.
     *
     * @param blockInformation The block information to append the tooltip for.
     * @param context The context.
     * @param tooltip The tooltip list.
     * @param flags The flags
     */
    void appendHoverText(BlockInformation blockInformation, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flags);
}
