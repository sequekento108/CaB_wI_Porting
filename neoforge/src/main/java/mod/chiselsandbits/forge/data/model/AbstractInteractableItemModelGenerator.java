package mod.chiselsandbits.forge.data.model;

import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import mod.chiselsandbits.api.util.constants.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;

public abstract class AbstractInteractableItemModelGenerator extends ItemModelProvider {
    private final IRegistryObject<? extends Item> targetRegistryObject;

    protected AbstractInteractableItemModelGenerator(
            final DataGenerator generator,
            final ExistingFileHelper existingFileHelper,
            final IRegistryObject<? extends Item> targetRegistryObject) {
        super(generator.getPackOutput(), Constants.MOD_ID, existingFileHelper);
        this.targetRegistryObject = targetRegistryObject;
    }

    @Override
    protected void registerModels() {
        final ResourceLocation targetRegistryObjectKey = BuiltInRegistries.ITEM.getKey(this.targetRegistryObject.get());
        final ResourceLocation targetModelLocation = targetRegistryObjectKey.withPrefix("item/").withSuffix("_spec");

        getBuilder(
                Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(this.targetRegistryObject.get())).getPath()
        )
                .parent(
                        getExistingFile(targetModelLocation)
                )
                .customLoader((itemModelBuilder, existingFileHelper) -> new CustomLoaderBuilder<>(
                        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "interactable_model"),
                        itemModelBuilder,
                        existingFileHelper,
                        false
                ) {
                });
    }
}
