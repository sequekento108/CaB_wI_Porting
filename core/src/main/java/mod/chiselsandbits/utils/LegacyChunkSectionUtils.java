package mod.chiselsandbits.utils;

import mod.chiselsandbits.api.block.storage.StateEntryStorage;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.util.IBatchMutation;
import mod.chiselsandbits.api.util.constants.NbtConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles the 1.16.5 chunk-section based block entity payload.
 * <p>
 * In 1.16.5 a chiseled block entity was stored as:
 * <pre>
 * {
 *   chiselBlockData: {
 *     compressedStorage: { palette: [...], blockStates: [...] }
 *       // or gzip compressed: { isCompressed: 1b, compressedData: [...] }
 *     statistics: { ... }
 *   }
 * }
 * </pre>
 * Later versions dropped the reader for this format (see issue #1387), which
 * wiped all chiseled blocks when upgrading a 1.16.5 world. This utility
 * restores the upgrade path by converting the legacy container into the
 * current {@link StateEntryStorage}.
 */
public final class LegacyChunkSectionUtils
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int SECTION_SIDE = 16;
    private static final int ENTRY_COUNT = SECTION_SIDE * SECTION_SIDE * SECTION_SIDE;

    private LegacyChunkSectionUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isLegacyChunkSectionPayload(@Nullable final CompoundTag blockEntityTag)
    {
        return blockEntityTag != null && blockEntityTag.contains(NbtConstants.CHISEL_BLOCK_ENTITY_DATA);
    }

    @Nullable
    public static StateEntryStorage loadStorageFromLegacy(
      @NotNull final CompoundTag blockEntityTag,
      @Nullable final HolderLookup.Provider provider)
    {
        final Tag chiselBlockDataTag = blockEntityTag.get(NbtConstants.CHISEL_BLOCK_ENTITY_DATA);
        if (!(chiselBlockDataTag instanceof CompoundTag chiselBlockData))
        {
            LOGGER.warn("Legacy chisel block data is not a compound tag, ignoring.");
            return null;
        }

        final Tag compressedStorageTag = chiselBlockData.get(NbtConstants.COMPRESSED_STORAGE);
        if (compressedStorageTag == null)
        {
            LOGGER.warn("Legacy chisel block data does not contain a compressed storage entry, ignoring.");
            return null;
        }

        final CompoundTag sectionTag = resolveSectionTag(compressedStorageTag);
        if (sectionTag == null)
        {
            return null;
        }

        final List<BlockState> palette = readPalette(sectionTag, provider);
        if (palette == null)
        {
            return null;
        }

        if (palette.isEmpty())
        {
            return new StateEntryStorage();
        }

        if (palette.size() == 1)
        {
            // Single valued container: vanilla stores no backing array in this case.
            final StateEntryStorage storage = new StateEntryStorage();
            storage.initializeWith(new BlockInformation(palette.get(0), Optional.empty()));
            return storage;
        }

        final long[] rawData = readBackingData(sectionTag);
        if (rawData == null || rawData.length == 0)
        {
            LOGGER.warn("Legacy chisel block data palette has {} entries but no backing long array, ignoring.", palette.size());
            return null;
        }

        return unpackIntoStorage(palette, rawData);
    }

    @Nullable
    private static CompoundTag resolveSectionTag(@NotNull final Tag compressedStorageTag)
    {
        if (!(compressedStorageTag instanceof CompoundTag compoundTag))
        {
            LOGGER.warn("Legacy compressed storage is not a compound tag, ignoring.");
            return null;
        }

        if (compoundTag.contains(NbtConstants.DATA_IS_COMPRESSED) && compoundTag.getBoolean(NbtConstants.DATA_IS_COMPRESSED))
        {
            if (!compoundTag.contains(NbtConstants.COMPRESSED_DATA, Tag.TAG_BYTE_ARRAY))
            {
                LOGGER.warn("Legacy compressed storage is marked as compressed but has no byte array, ignoring.");
                return null;
            }

            try
            {
                final byte[] compressedData = compoundTag.getByteArray(NbtConstants.COMPRESSED_DATA);
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
                return NbtIo.readCompressed(inputStream, NbtAccounter.unlimitedHeap());
            }
            catch (IOException e)
            {
                LOGGER.error("Failed to decompress legacy chiseled block data. Resetting data.", e);
                return null;
            }
        }

        return compoundTag;
    }

    @Nullable
    private static List<BlockState> readPalette(
      @NotNull final CompoundTag sectionTag,
      @Nullable final HolderLookup.Provider provider)
    {
        if (!sectionTag.contains(NbtConstants.PALETTE))
        {
            LOGGER.warn("Legacy chisel block data does not contain a palette, ignoring.");
            return null;
        }

        final Tag paletteTag = sectionTag.get(NbtConstants.PALETTE);
        if (!(paletteTag instanceof ListTag paletteList))
        {
            LOGGER.warn("Legacy chisel block palette is not a list, ignoring.");
            return null;
        }

        if (provider == null)
        {
            LOGGER.warn("No registry access available to decode legacy chisel block palette, ignoring.");
            return null;
        }

        final RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, provider);
        final List<BlockState> palette = new ArrayList<>(paletteList.size());
        for (final Tag entry : paletteList)
        {
            try
            {
                final BlockState state = BlockState.CODEC.parse(ops, entry)
                  .promotePartial(error -> LOGGER.warn("Failed to parse legacy palette entry, falling back to air: {}", error))
                  .result()
                  .orElse(Blocks.AIR.defaultBlockState());
                palette.add(state);
            }
            catch (Exception e)
            {
                LOGGER.warn("Failed to parse legacy palette entry, falling back to air.", e);
                palette.add(Blocks.AIR.defaultBlockState());
            }
        }

        return palette;
    }

    @Nullable
    private static long[] readBackingData(@NotNull final CompoundTag sectionTag)
    {
        if (sectionTag.contains(NbtConstants.LEGACY_BLOCK_STATES, Tag.TAG_LONG_ARRAY))
        {
            return sectionTag.getLongArray(NbtConstants.LEGACY_BLOCK_STATES);
        }

        // Newer containers (1.20+) use "data" for the same packed array.
        if (sectionTag.contains(NbtConstants.DATA, Tag.TAG_LONG_ARRAY))
        {
            return sectionTag.getLongArray(NbtConstants.DATA);
        }

        return null;
    }

    @NotNull
    private static StateEntryStorage unpackIntoStorage(
      @NotNull final List<BlockState> palette,
      @NotNull final long[] rawData)
    {
        final int paletteSize = palette.size();
        final int bits = Math.max(4, 32 - Integer.numberOfLeadingZeros(paletteSize - 1));
        final long mask = bits >= 64 ? -1L : (1L << bits) - 1L;

        final StateEntryStorage storage = new StateEntryStorage();
        try (IBatchMutation ignored = storage.batch())
        {
            for (int index = 0; index < ENTRY_COUNT; index++)
            {
                final int bitIndex = index * bits;
                final int startLong = bitIndex >> 6;

                int paletteIndex = 0;
                if (startLong < rawData.length)
                {
                    final int startBit = bitIndex & 63;
                    long value = rawData[startLong] >>> startBit;
                    if (startBit + bits > 64 && startLong + 1 < rawData.length)
                    {
                        value |= rawData[startLong + 1] << (64 - startBit);
                    }
                    paletteIndex = (int) (value & mask);
                }

                if (paletteIndex < 0 || paletteIndex >= paletteSize)
                {
                    paletteIndex = 0;
                }

                // Vanilla section indexing is (y << 8) | (z << 4) | x.
                final int x = index & 15;
                final int z = (index >> 4) & 15;
                final int y = (index >> 8) & 15;

                storage.setBlockInformation(
                  x, y, z,
                  new BlockInformation(palette.get(paletteIndex), Optional.empty()));
            }
        }

        return storage;
    }
}
