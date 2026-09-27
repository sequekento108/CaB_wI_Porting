package mod.chiselsandbits.forge.handler;

import com.mojang.logging.LogUtils;
import mod.chiselsandbits.ChiselsAndBits;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.block.ChiseledBlock;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AddPackFindersEventHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        event.addRepositorySource(registrar -> {
            try {
                Path coreJarPath = Path.of(ChiselsAndBits.class.getProtectionDomain().getCodeSource().getLocation().toURI());

                final PackLocationInfo packLocationInfo = new PackLocationInfo(
                        "chiselsandbits-core",
                        Component.literal("Chisels & Bits Core"),
                        PackSource.BUILT_IN,
                        Optional.of(
                                new KnownPack(Constants.MOD_ID, "core", ModList.get().getModFileById(Constants.MOD_ID).versionString())
                        )
                );

                final PackResources packResources = new PathPackResources(
                        packLocationInfo,
                        coreJarPath
                );

                final Pack pack = Pack.readMetaAndCreate(
                        packLocationInfo,
                        new SinglePackResourceResourcesSupplier(packResources),
                        event.getPackType(),
                        new PackSelectionConfig(true, Pack.Position.TOP, true)
                );

                registrar.accept(pack);
            } catch (URISyntaxException e) {
                LOGGER.error("Failed to inject Core Resource Pack. C&B Assets will not be loaded!", e);
            }
        });
    }

    private record SinglePackResourceResourcesSupplier(PackResources packResources) implements Pack.ResourcesSupplier {

        @Override
        public PackResources openPrimary(PackLocationInfo location) {
            return packResources();
        }

        @Override
        public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
            return packResources();
        }
    }
}
