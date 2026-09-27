package mod.chiselsandbits.utils;

import mod.chiselsandbits.api.util.constants.NbtConstants;
import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class LZ4DataCompressionUtils
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static CompoundTag compress(final Consumer<CompoundTag> uncompressedBuilder) {
        final CompoundTag result = new CompoundTag();

        compress(result, uncompressedBuilder);

        return result;
    }

    public static void compress(final CompoundTag compoundTag, final Consumer<CompoundTag> uncompressedBuilder) {
        final CompoundTag uncompressedData = new CompoundTag();

        uncompressedBuilder.accept(uncompressedData);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try
        {
            final OutputStream lz4Stream = new LZ4FrameOutputStream(outputStream);
            final DataOutput dataOutput = new DataOutputStream(lz4Stream);
            NbtIo.write(uncompressedData, dataOutput);
            lz4Stream.close();
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to compress data. Uncompressed data will be stored.", e);
            compoundTag.putBoolean(NbtConstants.COMPRESSED, false);
            compoundTag.put(NbtConstants.DATA, uncompressedData);
            return;
        }

        final byte[] compressedData = outputStream.toByteArray();
        try
        {
            outputStream.close();
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to close compression stream!", e);
        }
        compoundTag.putBoolean(NbtConstants.COMPRESSED, true);
        compoundTag.putByteArray(NbtConstants.DATA, compressedData);
    }

    public static void decompress(final CompoundTag input, final Consumer<CompoundTag> uncompressedConsumer) {
        if (!input.contains(NbtConstants.COMPRESSED) || !input.contains(NbtConstants.DATA)) {
            LOGGER.error("Received uncompressed data. This is normal during upgrade procedures, however in any other case this is an error!");
            uncompressedConsumer.accept(input);
            return;
        }

        if (!input.getBoolean(NbtConstants.COMPRESSED)) {
            LOGGER.error("Received uncompressed data. This is normal during upgrade procedures, however in any other case this is an error!");
            uncompressedConsumer.accept(input.getCompound(NbtConstants.DATA));
            return;
        }

        final byte[] compressedData = input.getByteArray(NbtConstants.DATA);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);

        final CompoundTag uncompressedData;
        try
        {
            final LZ4FrameInputStream lz4FrameInputStream = new LZ4FrameInputStream(byteArrayInputStream);
            final DataInput dataInput = new DataInputStream(lz4FrameInputStream);
            uncompressedData = NbtIo.read(dataInput);
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to decompress data. Uncompressed data will be loaded.", e);
            return;
        }

        uncompressedConsumer.accept(uncompressedData);
    }

    public static <P> P decompress(final CompoundTag input, final Function<CompoundTag, P> uncompressedConsumer) {
        if (!input.contains(NbtConstants.COMPRESSED) || !input.contains(NbtConstants.DATA)) {
            LOGGER.error("Received uncompressed data. This is normal during upgrade procedures, however in any other case this is an error!");
            return uncompressedConsumer.apply(input);
        }

        if (!input.getBoolean(NbtConstants.COMPRESSED)) {
            LOGGER.error("Received uncompressed data. This is normal during upgrade procedures, however in any other case this is an error!");
            return uncompressedConsumer.apply(input.getCompound(NbtConstants.DATA));
        }

        final byte[] compressedData = input.getByteArray(NbtConstants.DATA);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);

        CompoundTag uncompressedData = input;
        try
        {
            final LZ4FrameInputStream lz4FrameInputStream = new LZ4FrameInputStream(byteArrayInputStream);
            final DataInput dataInput = new DataInputStream(lz4FrameInputStream);
            uncompressedData = NbtIo.read(dataInput);
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to decompress data. Uncompressed data will be loaded.", e);
        }

        return uncompressedConsumer.apply(uncompressedData);
    }
}
