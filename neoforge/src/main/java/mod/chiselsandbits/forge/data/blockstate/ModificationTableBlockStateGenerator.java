package mod.chiselsandbits.forge.data.blockstate;

import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.registrars.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModificationTableBlockStateGenerator extends BlockStateProvider implements DataProvider
{
    private static final ResourceLocation MODIFICATION_TABLE_BLOCK_MODEL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "block/modification_table");

    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event)
    {
        event.getGenerator().addProvider(true, new ModificationTableBlockStateGenerator(event.getGenerator(), event.getExistingFileHelper()));
    }

    public ModificationTableBlockStateGenerator(final DataGenerator gen, final ExistingFileHelper exFileHelper)
    {
        super(gen.getPackOutput(), Constants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        horizontalBlock(
          ModBlocks.MODIFICATION_TABLE.get(),
          models().getExistingFile(
            MODIFICATION_TABLE_BLOCK_MODEL
          ),
          0
        );
    }

    @Override
    public @NotNull String getName()
    {
        return "Modification station blockstate generator.";
    }
}
