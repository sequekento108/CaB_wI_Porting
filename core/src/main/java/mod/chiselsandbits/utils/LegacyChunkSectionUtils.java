package mod.chiselsandbits.utils;

import mod.chiselsandbits.api.block.storage.StateEntryStorage;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.util.IBatchMutation;
import mod.chiselsandbits.api.util.constants.NbtConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
 * <p>
 * Palette entries are decoded leniently and rewritten through
 * {@link LegacyBlockRemapper} first, so blocks from removed mods can be
 * migrated via user-provided remap files instead of turning to air.
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

        final HolderGetter<Block> blocks;
        try
        {
            blocks = provider.lookupOrThrow(Registries.BLOCK);
        }
        catch (Exception e)
        {
            LOGGER.warn("No block registry available to decode legacy chisel block palette, ignoring.", e);
            return null;
        }

        final LegacyBlockRemapper remapper = LegacyBlockRemapper.getInstance();
        final Set<String> unknownIds = new LinkedHashSet<>();
        int droppedProperties = 0;

        final List<BlockState> palette = new ArrayList<>(paletteList.size());
        for (final Tag entry : paletteList)
        {
            if (!(entry instanceof CompoundTag compoundEntry))
            {
                unknownIds.add("<malformed>");
                palette.add(Blocks.AIR.defaultBlockState());
                continue;
            }

            final DecodeResult result = decodePaletteEntry(compoundEntry, blocks, remapper);
            if (result.unknownId() != null)
            {
                unknownIds.add(result.unknownId());
            }
            droppedProperties += result.droppedProperties();
            palette.add(result.state());
        }

        if (!unknownIds.isEmpty())
        {
            final List<String> shown = new ArrayList<>(unknownIds);
            final String listed = String.join(", ", shown.subList(0, Math.min(shown.size(), 10)));
            LOGGER.warn("Legacy chisel block data references {} unknown block(s): {}{}. Those bits will load as air. To migrate them, add remap files under {} or {} (RemapIDs-compatible).",
              unknownIds.size(), listed, shown.size() > 10 ? ", ..." : "", LegacyBlockRemapper.remapIdsDirectory(), LegacyBlockRemapper.remapDirectory());
        }
        if (droppedProperties > 0)
        {
            LOGGER.debug("Dropped {} incompatible legacy blockstate properties while upgrading chisel block data.", droppedProperties);
        }

        return palette;
    }

    private record DecodeResult(BlockState state, @Nullable String unknownId, int droppedProperties)
    {
    }

    /**
     * Decodes a single {@code {Name, Properties}} palette entry leniently: the ID is first
     * rewritten through the user remaps, unknown blocks fall back to air, and properties the
     * target block does not understand are dropped instead of discarding the whole entry.
     */
    @NotNull
    private static DecodeResult decodePaletteEntry(
      @NotNull final CompoundTag entry,
      @NotNull final HolderGetter<Block> blocks,
      @NotNull final LegacyBlockRemapper remapper)
    {
        final String name = entry.contains("Name", Tag.TAG_STRING) ? entry.getString("Name") : "";
        final String remappedName = remapper.remapBlockId(name);

        Block block = null;
        final ResourceLocation id = ResourceLocation.tryParse(remappedName);
        if (id != null)
        {
            try
            {
                final Optional<Holder.Reference<Block>> holder = blocks.get(ResourceKey.create(Registries.BLOCK, id));
                if (holder.isPresent())
                {
                    block = holder.get().value();
                }
            }
            catch (Exception e)
            {
                LOGGER.debug("Failed to resolve legacy palette entry {}, treating as air.", remappedName, e);
            }
        }

        if (block == null)
        {
            return new DecodeResult(Blocks.AIR.defaultBlockState(), name.isEmpty() ? "<missing>" : name, 0);
        }

        BlockState state = block.defaultBlockState();
        int dropped = 0;

        final Tag propertiesTag = entry.get("Properties");
        if (propertiesTag instanceof CompoundTag properties)
        {
            for (final String key : properties.getAllKeys())
            {
                if (!properties.contains(key, Tag.TAG_STRING))
                {
                    dropped++;
                    continue;
                }
                final Property<?> property = block.getStateDefinition().getProperty(key);
                if (property == null)
                {
                    dropped++;
                    continue;
                }
                final BlockState updated = withProperty(state, property, properties.getString(key));
                if (updated == null)
                {
                    dropped++;
                }
                else
                {
                    state = updated;
                }
            }
        }

        return new DecodeResult(state, null, dropped);
    }

    @Nullable
    private static <T extends Comparable<T>> BlockState withProperty(final BlockState state, final Property<T> property, final String value)
    {
        final Optional<T> parsed = property.getValue(value);
        if (parsed.isEmpty())
        {
            return null;
        }
        try
        {
            return state.setValue(property, parsed.get());
        }
        catch (Exception e)
        {
            return null;
        }
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
