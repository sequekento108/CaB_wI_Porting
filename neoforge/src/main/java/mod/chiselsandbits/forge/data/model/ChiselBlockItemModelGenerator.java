package mod.chiselsandbits.forge.data.model;

import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.registrars.ModBlocks;
import mod.chiselsandbits.registrars.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ChiselBlockItemModelGenerator extends ItemModelProvider implements DataProvider {
    private static final ResourceLocation LOADER = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "chiseled_block");

    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event) {
        event.getGenerator().addProvider(true, new ChiselBlockItemModelGenerator(event.getGenerator(), event.getExistingFileHelper()));
    }

    public ChiselBlockItemModelGenerator(final DataGenerator generator, final ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        actOnBlockWithLoader(ModBlocks.CHISELED_BLOCK.get());
        actOnItemWithLoader(ModItems.CHISELED_BLOCK.get());

        ModItems.LEGACY_MATERIAL_CHISELED_BLOCKS.stream()
                        .map(IRegistryObject::get)
                                .forEach(item -> {
                                    getBuilder(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).getPath())
                                            .parent(getExistingFile(ResourceLocation.withDefaultNamespace("item/generated")));
                                });
    }

    public void actOnBlockWithLoader(final Block block) {
        getBuilder(
                Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath()
        )
                .parent(getExistingFile(ResourceLocation.withDefaultNamespace("item/generated")))
                .customLoader((itemModelBuilder, existingFileHelper) -> new CustomLoaderBuilder<>(LOADER, itemModelBuilder, existingFileHelper, false) {
                });
    }

    public void actOnItemWithLoader(final Item item) {
        getBuilder(
                Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).getPath()
        )
                .parent(getExistingFile(ResourceLocation.withDefaultNamespace("item/generated")))
                .customLoader((itemModelBuilder, existingFileHelper) -> new CustomLoaderBuilder<>(LOADER, itemModelBuilder, existingFileHelper, false) {
                });
    }

    @NotNull
    @Override
    public String getName() {
        return "Chisel block item model generator";
    }
}
