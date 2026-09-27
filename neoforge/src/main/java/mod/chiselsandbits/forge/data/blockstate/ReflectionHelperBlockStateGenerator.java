package mod.chiselsandbits.forge.data.blockstate;

import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.registrars.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ReflectionHelperBlockStateGenerator extends BlockStateProvider implements DataProvider
{
    public ReflectionHelperBlockStateGenerator(final DataGenerator gen, final ExistingFileHelper exFileHelper)
    {
        super(gen.getPackOutput(), Constants.MOD_ID, exFileHelper);
    }

    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event)
    {
        event.getGenerator().addProvider(true, new ReflectionHelperBlockStateGenerator(event.getGenerator(), event.getExistingFileHelper()));
    }

    @Override
    protected void registerStatesAndModels()
    {
        this.actOnBlock(ModBlocks.REFLECTION_HELPER_BLOCK.get());
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Reflection helper block blockstate generator";
    }

    public void actOnBlock(final Block block)
    {
        getVariantBuilder(block)
          .forAllStates(blockState -> ConfiguredModel.builder()
            .modelFile(models().getExistingFile(ResourceLocation.withDefaultNamespace("air")))
            .build());
    }
}
