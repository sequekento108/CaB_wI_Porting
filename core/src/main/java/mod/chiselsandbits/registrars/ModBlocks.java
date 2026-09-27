package mod.chiselsandbits.registrars;

import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import com.google.common.collect.Maps;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.block.*;
import mod.chiselsandbits.compact.legacy.LegacyMaterialManager;
import mod.chiselsandbits.compact.legacy.block.MateriallyChiseledConversionBlock;
import mod.chiselsandbits.utils.ReflectionHelperBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public final class ModBlocks
{

    private static final Logger                                        LOGGER                        = LogManager.getLogger();
    private static final IRegistrar<Block> BLOCK_REGISTRAR               = IRegistrar.create(Registries.BLOCK, Constants.MOD_ID);
    public static final IRegistryObject<BitStorageBlock> BIT_STORAGE =
      BLOCK_REGISTRAR.register("bit_storage", () -> new BitStorageBlock(BlockBehaviour.Properties.of()
        .strength(1.5F, 6.0F)
        .requiresCorrectToolForDrops()
        .dynamicShape()
        .noOcclusion()
        .isValidSpawn((p_test_1_, p_test_2_, p_test_3_, p_test_4_) -> false)
        .isRedstoneConductor((p_test_1_, p_test_2_, p_test_3_) -> false)
        .isSuffocating((p_test_1_, p_test_2_, p_test_3_) -> false)
        .isViewBlocking((p_test_1_, p_test_2_, p_test_3_) -> false)));

    public static final IRegistryObject<ModificationTableBlock> MODIFICATION_TABLE =
      BLOCK_REGISTRAR.register("modification_table", () -> new ModificationTableBlock(BlockBehaviour.Properties.of()
        .strength(1.5F, 6.0F)
        .requiresCorrectToolForDrops()
        .dynamicShape()
        .noOcclusion()
        .isValidSpawn((p_test_1_, p_test_2_, p_test_3_, p_test_4_) -> false)
        .isRedstoneConductor((p_test_1_, p_test_2_, p_test_3_) -> false)
        .isSuffocating((p_test_1_, p_test_2_, p_test_3_) -> false)
        .isViewBlocking((p_test_1_, p_test_2_, p_test_3_) -> false)));

    public static final IRegistryObject<ChiseledPrinterBlock> CHISELED_PRINTER = BLOCK_REGISTRAR.register(
      "chiseled_printer",
      () -> new ChiseledPrinterBlock(BlockBehaviour.Properties.of()
        .strength(1.5F, 6.0F)
        .requiresCorrectToolForDrops()
        .dynamicShape()
        .noOcclusion()
        .isValidSpawn((p_test_1_, p_test_2_, p_test_3_, p_test_4_) -> false)
        .isRedstoneConductor((p_test_1_, p_test_2_, p_test_3_) -> false)
        .isSuffocating((p_test_1_, p_test_2_, p_test_3_) -> false)
        .isViewBlocking((p_test_1_, p_test_2_, p_test_3_) -> false))
    );

    public static final IRegistryObject<PatternScannerBlock> PATTERN_SCANNER = BLOCK_REGISTRAR.register(
      "pattern_scanner",
      () -> new PatternScannerBlock(BlockBehaviour.Properties.of()
        .strength(1.5F, 6.0F)
        .requiresCorrectToolForDrops()
        .dynamicShape()
        .noOcclusion()
        .isValidSpawn((p_test_1_, p_test_2_, p_test_3_, p_test_4_) -> false)
        .isRedstoneConductor((p_test_1_, p_test_2_, p_test_3_) -> false)
        .isSuffocating((p_test_1_, p_test_2_, p_test_3_) -> false)
        .isViewBlocking((p_test_1_, p_test_2_, p_test_3_) -> false))
    );

    public static IRegistryObject<ReflectionHelperBlock> REFLECTION_HELPER_BLOCK = BLOCK_REGISTRAR.register(
            "reflection_helper_block",
            ReflectionHelperBlock::new
    );

    public static IRegistryObject<Block> CHISELED_BLOCK = BLOCK_REGISTRAR.register(
            "chiseled",
            () -> new ChiseledBlock(BlockBehaviour.Properties.of()
                    .strength(1.5f, 6f)
                    .isRedstoneConductor((p_test_1_, p_test_2_, p_test_3_) -> false)
                    .isValidSpawn((p_test_1_, p_test_2_, p_test_3_, p_test_4_) -> false)
                    .isSuffocating((p_test_1_, p_test_2_, p_test_3_) -> false)
                    .noOcclusion())
    );

    public static final Map<String, IRegistryObject<Block>> MATERIAL_TO_BLOCK_CONVERSIONS = Maps.newHashMap();


    private ModBlocks()
    {
        throw new IllegalStateException("Tried to initialize: ModBlocks but this is a Utility class.");
    }

    public static void onModConstruction()
    {
        LegacyMaterialManager.getInstance()
                .getMaterialNames()
                .forEach((name) -> {
                    MATERIAL_TO_BLOCK_CONVERSIONS.put(name, BLOCK_REGISTRAR.register("chiseled" + name, () -> new MateriallyChiseledConversionBlock(BlockBehaviour.Properties.of()
                            .strength(1.5f, 6f)
                            .isRedstoneConductor((p_test_1_, p_test_2_, p_test_3_) -> false)
                            .isValidSpawn((p_test_1_, p_test_2_, p_test_3_, p_test_4_) -> false)
                            .isSuffocating((p_test_1_, p_test_2_, p_test_3_) -> false)
                            .noOcclusion())));
                });
        LOGGER.info("Loaded block configuration.");
    }
}
