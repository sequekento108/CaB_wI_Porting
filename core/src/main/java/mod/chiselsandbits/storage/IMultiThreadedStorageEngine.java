package mod.chiselsandbits.storage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Represents a multithreaded storage engine, which can process data for IO purposes.
 */
public interface IMultiThreadedStorageEngine<TPayload>
{

    /**
     * Encodes the given payload off-thread.
     *
     * @param payload The payload to encode.
     * @param provider The holder lookup provider.
     * @return The off-thread encode task.
     */
    CompletableFuture<Tag> encodeAsync(TPayload payload, HolderLookup.Provider provider);

    /**
     * Decodes the given nbt data off-thread.
     *
     * @param tag The tag to decode.
     * @param provider The holder lookup provider.
     * @return The off-thread decode task.
     */
    CompletableFuture<TPayload> decodeAsync(Tag tag, HolderLookup.Provider provider);
}
