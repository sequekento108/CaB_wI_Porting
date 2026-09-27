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
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PatternScannerBlockStateGenerator extends BlockStateProvider implements DataProvider
{
    private static final ResourceLocation PATTERN_SCANNER_BLOCK_MODEL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "block/pattern_scanner");

    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event)
    {
        event.getGenerator().addProvider(true, new PatternScannerBlockStateGenerator(event.getGenerator(), event.getExistingFileHelper()));
    }


    public PatternScannerBlockStateGenerator(final DataGenerator gen, final ExistingFileHelper exFileHelper)
    {
        super(gen.getPackOutput(), Constants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        actOnBlock(ModBlocks.PATTERN_SCANNER.get());
    }

    public void actOnBlock(final Block block)
    {
        horizontalBlock(block, models().getExistingFile(PATTERN_SCANNER_BLOCK_MODEL));
    }

    @Override
    public @NotNull String getName()
    {
        return "Pattern scanner blockstate generator";
    }
}
