package mod.chiselsandbits.forge.data.tag;

import mod.chiselsandbits.api.util.constants.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.internal.NeoForgeBlockTagsProvider;
import net.neoforged.neoforge.common.data.internal.NeoForgeItemTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class TagGeneratorEventHandler
{

    @SuppressWarnings("UnstableApiUsage")
    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        ModBlockTagGenerator modBlockTags = new ModBlockTagGenerator(packOutput, lookupProvider, existingFileHelper);
        event.getGenerator().addProvider(true, modBlockTags);
        event.getGenerator().addProvider(true, new ModItemTagGenerator(packOutput, lookupProvider, modBlockTags.contentsGetter(), existingFileHelper));
    }
}
