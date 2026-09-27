package mod.chiselsandbits.forge.data.model;

import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.registrars.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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
public class BlockBitItemModelGenerator extends ItemModelProvider implements DataProvider {
    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event) {
        event.getGenerator().addProvider(true, new BlockBitItemModelGenerator(event.getGenerator(), event.getExistingFileHelper()));
    }

    public BlockBitItemModelGenerator(final DataGenerator generator, final ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        actOnBlockWithLoader(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "bit"), ModItems.ITEM_BLOCK_BIT.get());
    }

    public void actOnBlockWithLoader(final ResourceLocation loader, final Item item) {
        getBuilder(
                Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).getPath()
        )
                .parent(getExistingFile(ResourceLocation.withDefaultNamespace("item/generated")))
                .customLoader((itemModelBuilder, existingFileHelper) -> new CustomLoaderBuilder<>(loader, itemModelBuilder, existingFileHelper, false) {
                });
    }

    @NotNull
    @Override
    public String getName() {
        return "Bit item model generator";
    }
}
