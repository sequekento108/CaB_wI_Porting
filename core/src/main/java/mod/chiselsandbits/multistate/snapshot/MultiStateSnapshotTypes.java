package mod.chiselsandbits.multistate.snapshot;

import com.mojang.serialization.MapCodec;
import mod.chiselsandbits.api.multistate.snapshot.IMultiStateSnapshot;
import mod.chiselsandbits.api.multistate.snapshot.IMultiStateSnapshotType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public enum MultiStateSnapshotTypes implements IMultiStateSnapshotType {
    EMPTY(ResourceLocation.fromNamespaceAndPath("chiselsandbits", "empty"), MapCodec.unit(EmptySnapshot.INSTANCE), StreamCodec.unit(EmptySnapshot.INSTANCE)),
    MULTI_BLOCK(ResourceLocation.fromNamespaceAndPath("chiselsandbits", "multi_block"), MultiBlockMultiStateSnapshot.MAP_CODEC, MultiBlockMultiStateSnapshot.STREAM_CODEC),
    SIMPLE(ResourceLocation.fromNamespaceAndPath("chiselsandbits", "simple"), SimpleSnapshot.MAP_CODEC, SimpleSnapshot.STREAM_CODEC);

    private final ResourceLocation registryName;
    private final MapCodec<? extends IMultiStateSnapshot> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, ? extends IMultiStateSnapshot> streamCodec;

    MultiStateSnapshotTypes(ResourceLocation registryName, MapCodec<? extends IMultiStateSnapshot> codec, StreamCodec<RegistryFriendlyByteBuf, ? extends IMultiStateSnapshot> streamCodec) {
        this.registryName = registryName;
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public MapCodec<? extends IMultiStateSnapshot> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends IMultiStateSnapshot> streamCodec() {
        return streamCodec;
    }
}
