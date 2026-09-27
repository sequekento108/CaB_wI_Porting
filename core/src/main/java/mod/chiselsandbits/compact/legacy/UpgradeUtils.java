package mod.chiselsandbits.compact.legacy;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.chiseling.mode.IChiselMode;
import mod.chiselsandbits.api.util.BlockStateSerializationUtils;
import mod.chiselsandbits.api.util.constants.NbtConstants;
import mod.chiselsandbits.api.variant.state.IStateVariant;
import mod.chiselsandbits.components.data.MultiStateItemStackData;
import mod.chiselsandbits.registrars.ModDataComponentTypes;
import mod.chiselsandbits.registrars.ModItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.util.Optional;

@Deprecated
public class UpgradeUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    private UpgradeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void upgradeBitItem(ItemStack stack) {
        if (!stack.has(ModDataComponentTypes.CHISEL_MODE.get()) && stack.has(DataComponents.CUSTOM_DATA)) {
            final CustomData storedData = stack.get(DataComponents.CUSTOM_DATA);
            final CompoundTag tag = storedData.getUnsafe();

            String chiselModeName = tag.getString(NbtConstants.CHISEL_MODE);
            if (chiselModeName.isEmpty()) {
                chiselModeName = tag.getString("chiselMode");
            }
            try {
                final Optional<IChiselMode> registryMode = IChiselMode.getRegistry().get(ResourceLocation.tryParse(chiselModeName));
                if (registryMode.isPresent()) {
                    ModItems.ITEM_BLOCK_BIT.get().setMode(stack, registryMode.get());
                } else {
                    LOGGER.error("Unknown chisel mode: {}", chiselModeName);
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                LOGGER.error("An ItemStack got loaded with a name that is not a valid chisel mode: {}", chiselModeName);
                ModItems.ITEM_BLOCK_BIT.get().setMode(stack, IChiselMode.getDefaultMode());
            }
        }

        if (!stack.has(ModDataComponentTypes.BLOCK_INFORMATION.get()) && stack.has(DataComponents.CUSTOM_DATA)) {
            final CustomData storedData = stack.get(DataComponents.CUSTOM_DATA);
            final CompoundTag tag = storedData.getUnsafe();

            if (tag.contains(NbtConstants.BLOCK_INFORMATION) && tag.get(NbtConstants.BLOCK_INFORMATION) instanceof CompoundTag nbt) {
                DataResult<BlockState> dataResult = BlockStateSerializationUtils.deserialize(nbt.getString(NbtConstants.STATE));
                var blockState = dataResult.result().orElseGet(Blocks.AIR::defaultBlockState);

                Optional<IStateVariant> variant = Optional.empty();
                if (nbt.contains(NbtConstants.VARIANT) && nbt.get(NbtConstants.VARIANT) instanceof CompoundTag variantTag) {
                    try {
                        variant = Optional.of(IStateVariant.CODEC.decode(NbtOps.INSTANCE, variantTag).getPartialOrThrow()
                                .getFirst());
                    } catch (IllegalArgumentException illegalArgumentException) {
                        LOGGER.error("An ItemStack got loaded with an incompatible bit variant.");
                    }
                }

                stack.set(ModDataComponentTypes.BLOCK_INFORMATION.get(), new BlockInformation(blockState, variant));
            }
        }
    }

    public static ItemStack UpgradeChiseledBlockItemStack(ItemStack sourceStack, RegistryAccess registryAccess) {
        if (!(sourceStack.getItem() instanceof MateriallyChiseledConversionItem))
            throw new IllegalStateException("Item is not a MateriallyChiseledConversionItem");

        if (!sourceStack.has(ModDataComponentTypes.MULTI_STATE_ITEM_STACK_DATA.get()) &&
                sourceStack.has(DataComponents.CUSTOM_DATA)
        ) {
            final CustomData storedData = sourceStack.get(DataComponents.CUSTOM_DATA);
            final CompoundTag tag = storedData.getUnsafe();
            if (tag.contains(NbtConstants.LEGACY_CHISELED_DATA) && tag.get(NbtConstants.LEGACY_CHISELED_DATA) instanceof CompoundTag nbt) {
                try {
                    final MultiStateItemStackData data = MultiStateItemStackData.CODEC
                            .decode(NbtOps.INSTANCE, nbt)
                            .getPartialOrThrow()
                            .getFirst();
                    sourceStack.set(ModDataComponentTypes.MULTI_STATE_ITEM_STACK_DATA.get(), data);

                    return new ItemStack(
                            registryAccess.registry(Registries.ITEM)
                                    .get().wrapAsHolder(ModItems.CHISELED_BLOCK.get())
                            , sourceStack.getCount(), sourceStack.getComponentsPatch());
                } catch (Exception e) {
                    LOGGER.error("An ItemStack got loaded with an incompatible bit multi state.");
                }
            }
        }

        return sourceStack;
    }
}
