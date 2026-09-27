package mod.chiselsandbits.forge.data.recipe;

import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.registrars.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ChiselRecipeGenerator extends AbstractChiselRecipeGenerator
{

    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event)
    {
        event.getGenerator().addProvider(true, new ChiselRecipeGenerator(event.getGenerator().getPackOutput(), ModItems.ITEM_CHISEL_STONE.get(), Tags.Items.STONES, event.getLookupProvider()));
        event.getGenerator().addProvider(true, new ChiselRecipeGenerator(event.getGenerator().getPackOutput(), ModItems.ITEM_CHISEL_IRON.get(), Tags.Items.INGOTS_IRON, event.getLookupProvider()));
        event.getGenerator().addProvider(true, new ChiselRecipeGenerator(event.getGenerator().getPackOutput(), ModItems.ITEM_CHISEL_GOLD.get(), Tags.Items.INGOTS_GOLD, event.getLookupProvider()));
        event.getGenerator().addProvider(true, new ChiselRecipeGenerator(event.getGenerator().getPackOutput(), ModItems.ITEM_CHISEL_DIAMOND.get(), Tags.Items.GEMS_DIAMOND, event.getLookupProvider()));
        event.getGenerator().addProvider(true, new ChiselRecipeGenerator(event.getGenerator().getPackOutput(), ModItems.ITEM_CHISEL_NETHERITE.get(), Tags.Items.RODS_BLAZE, Tags.Items.INGOTS_NETHERITE, event.getLookupProvider()));
    }

    private ChiselRecipeGenerator(
      final PackOutput generator,
      final Item result,
      final TagKey<Item> ingredientTag,
      final CompletableFuture<HolderLookup.Provider> registries)
    {
        super(generator, result, ingredientTag, registries);
    }

    private ChiselRecipeGenerator(final PackOutput generator,
                                  final Item result,
                                  final TagKey<Item> rodTag,
                                  final TagKey<Item> ingredientTag,
                                  final CompletableFuture<HolderLookup.Provider> registries)
    {
        super(generator, result, rodTag, ingredientTag, registries);
    }
}
