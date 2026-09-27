package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.network.handlers.ClientPacketHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public final class NeighborBlockUpdatedPacket extends ModPacket
{

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "neighbor_block_updated");
    public static final CustomPacketPayload.Type<NeighborBlockUpdatedPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    private BlockPos toUpdate = BlockPos.ZERO;
    private BlockPos from     = BlockPos.ZERO;

    public NeighborBlockUpdatedPacket(final BlockPos toUpdate, final BlockPos from)
    {
        super();
        this.toUpdate = toUpdate;
        this.from = from;
    }

    public NeighborBlockUpdatedPacket(RegistryFriendlyByteBuf buffer)
    {
        super();
        readPayload(buffer);
    }

    @Override
    public void writePayload(final RegistryFriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.toUpdate);
        buffer.writeBlockPos(this.from);
    }

    @Override
    public void readPayload(final RegistryFriendlyByteBuf buffer)
    {
        this.toUpdate = buffer.readBlockPos();
        this.from = buffer.readBlockPos();
    }

    @Override
    public void client()
    {
        ClientPacketHandlers.handleNeighborUpdated(toUpdate, from);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
