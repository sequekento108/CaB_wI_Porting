package mod.chiselsandbits.registrars;

import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.block.entities.BitStorageBlockEntity;
import mod.chiselsandbits.block.entities.ChiseledBlockEntity;
import mod.chiselsandbits.block.entities.ChiseledPrinterBlockEntity;
import mod.chiselsandbits.compact.legacy.block.entity.MateriallyChiseledConversionBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@SuppressWarnings("ConstantConditions")
public final class ModBlockEntityTypes
{
    private static final Logger                         LOGGER    = LogManager.getLogger();
    private static final IRegistrar<BlockEntityType<?>> REGISTRAR = IRegistrar.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    private ModBlockEntityTypes()
    {
        throw new IllegalStateException("Tried to initialize: ModTileEntityTypes but this is a Utility class.");
    }

    public static void onModConstruction()
    {
        LOGGER.info("Loaded block entity configuration.");
    }

    public static IRegistryObject<BlockEntityType<ChiseledBlockEntity>> CHISELED = REGISTRAR.register("chiseled_block", () -> BlockEntityType.Builder.of(
        ChiseledBlockEntity::new,
        ModBlocks.CHISELED_BLOCK.get()
      ).build(null)
    );

    public static IRegistryObject<BlockEntityType<BitStorageBlockEntity>> BIT_STORAGE = REGISTRAR.register("bit_storage", () -> BlockEntityType.Builder.of(
        BitStorageBlockEntity::new,
        ModBlocks.BIT_STORAGE.get()
      ).build(null)
    );

    public static final IRegistryObject<BlockEntityType<ChiseledPrinterBlockEntity>> CHISELED_PRINTER = REGISTRAR.register(
      "chiseled_printer",
      () -> BlockEntityType.Builder.of(
        ChiseledPrinterBlockEntity::new,
        ModBlocks.CHISELED_PRINTER.get()
      ).build(null)
    );

    @Deprecated
    public static final IRegistryObject<BlockEntityType<MateriallyChiseledConversionBlockEntity>> MATERIAL_CHISELED_CONVERSION = REGISTRAR.register("chiseled", () -> new BlockEntityType<>(
            MateriallyChiseledConversionBlockEntity::new,
            Set.of(),
            null
    ) {
        @Override
        public boolean isValid(@NotNull BlockState state) {
            return ModBlocks.MATERIAL_TO_BLOCK_CONVERSIONS.values()
                    .stream()
                    .anyMatch(blockRegistryEntry -> blockRegistryEntry.get() == state.getBlock());
        }
    });
}