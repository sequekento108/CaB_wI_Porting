package mod.chiselsandbits.forge.data.model;

import mod.chiselsandbits.api.util.constants.Constants;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class MeasuringTapeModelGenerator extends ItemModelProvider
{
    private MeasuringTapeModelGenerator(final DataGenerator generator, final ExistingFileHelper existingFileHelper)
    {
        super(generator.getPackOutput(), Constants.MOD_ID, existingFileHelper);
    }

    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event)
    {
        event.getGenerator().addProvider(true, new MeasuringTapeModelGenerator(event.getGenerator(), event.getExistingFileHelper()));
    }

    @Override
    protected void registerModels()
    {
        getBuilder("measuring_tape")
          .parent(new ModelFile.UncheckedModelFile("item/generated"))
          .texture("layer0", ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/tape_measure"))
          .transforms()
          .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
          .rotation(-80, 260, -40)
          .translation(-1, -2, 2.5f)
          .scale(0.9f, 0.9f, 0.9f)
          .end()
          .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
          .rotation(-80, -280, 40)
          .translation(-1, -2, 2.5f)
          .scale(0.9f, 0.9f, 0.9f)
          .end()
          .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
          .rotation(0,-90,25)
          .translation(1.13f, 3.2f, 1.13f)
          .scale(0.68f,0.68f,0.68f)
          .end()
          .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
          .rotation(0,90,-25)
          .translation(1.13f, 3.2f, 1.13f)
          .scale(0.68f,0.68f,0.68f)
          .end()
          .end()
          .override()
          .predicate(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "is_measuring"), 1)
          .model(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/measuring_tape_is_measuring")))
          .end();

        getBuilder("measuring_tape_is_measuring")
          .parent(getBuilder("measuring_tape"))
          .texture("layer0", "item/tape_measure_is_measuring");
    }
}
