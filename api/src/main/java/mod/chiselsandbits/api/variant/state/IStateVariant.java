package mod.chiselsandbits.api.variant.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import mod.chiselsandbits.api.util.ISnapshotable;
import mod.chiselsandbits.api.serialization.Serializable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Object which provides additional information about a state.
 * <p>
 *     This has to be immutable as it is used in data components in the game.
 */
public interface IStateVariant extends Comparable<IStateVariant>, ISnapshotable<IStateVariant> {
    /**
     * The codec that can be used to serialize a state variant.
     */
    Codec<IStateVariant> CODEC = IStateVariantManager.getInstance().byNameCodec()
            .dispatch(IStateVariant::provider, IStateVariantProvider::mapCodec);

    /**
     * The map codec that can be used to serialize a state variant.
     */
    MapCodec<IStateVariant> MAP_CODEC = IStateVariantManager.getInstance().byNameCodec()
            .dispatchMap(IStateVariant::provider, IStateVariantProvider::mapCodec);

    /**
     * The stream codec that can be used to serialize a state variant.
     */
    StreamCodec<RegistryFriendlyByteBuf, IStateVariant> STREAM_CODEC = IStateVariantManager.getInstance().byNameStreamCodec()
            .dispatch(IStateVariant::provider, IStateVariantProvider::streamCodec);


    /**
     * {@return The provider that created this state variant.}
     */
    IStateVariantProvider provider();
}
