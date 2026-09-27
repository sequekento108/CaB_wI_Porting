package mod.chiselsandbits.registrars;

import mod.chiselsandbits.api.item.withmode.group.IToolModeGroup;
import mod.chiselsandbits.api.util.LocalStrings;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.utils.TranslationUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ModChiselModeGroups
{

    private static final Logger LOGGER = LogManager.getLogger();

    public static IToolModeGroup CUBED = new IToolModeGroup()
    {
        @Override
        public @NotNull ResourceLocation getIcon()
        {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/cube_medium.png");
        }

        @Override
        public Component getDisplayName()
        {
            return TranslationUtils.build(
              "chiselmode.cubed"
            );
        }
    };

    public static IToolModeGroup CUBED_ALIGNED = new IToolModeGroup()
    {
        @Override
        public @NotNull ResourceLocation getIcon()
        {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/snap4.png");
        }

        @Override
        public Component getDisplayName()
        {
            return TranslationUtils.build(
              "chiselmode.snap"
            );
        }
    };

    public static IToolModeGroup LINE = new IToolModeGroup()
    {
        @Override
        public @NotNull ResourceLocation getIcon()
        {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/line.png");
        }

        @Override
        public Component getDisplayName()
        {
            return TranslationUtils.build(
              "chiselmode.line"
            );
        }
    };

    public static IToolModeGroup PLANE = new IToolModeGroup()
    {
        @Override
        public @NotNull ResourceLocation getIcon()
        {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/plane.png");
        }

        @Override
        public Component getDisplayName()
        {
            return TranslationUtils.build(
              "chiselmode.plane"
            );
        }
    };

    public static IToolModeGroup SPHERE = new IToolModeGroup()
    {
        @Override
        public @NotNull ResourceLocation getIcon()
        {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/sphere_medium.png");
        }

        @Override
        public Component getDisplayName()
        {
            return TranslationUtils.build(
              "chiselmode.sphere"
            );
        }
    };

    public static IToolModeGroup CONNECTED_PLANE = new IToolModeGroup()
    {
        @Override
        public @NotNull ResourceLocation getIcon()
        {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/connected_plane.png");
        }

        @Override
        public Component getDisplayName()
        {
            return LocalStrings.ChiselModeConnectedPlane.getText();
        }
    };

    public static IToolModeGroup CONNECTED_MATERIAL = new IToolModeGroup()
    {
        @Override
        public @NotNull ResourceLocation getIcon()
        {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/connected_material.png");
        }

        @Override
        public Component getDisplayName()
        {
            return LocalStrings.ChiselModeConnectedMaterial.getText();
        }
    };

    public static IToolModeGroup DRAW = new IToolModeGroup()
    {
        @Override
        public @NotNull ResourceLocation getIcon()
        {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/drawn_cube.png");
        }

        @Override
        public Component getDisplayName()
        {
            return LocalStrings.ChiselModeDrawnCube.getText();
        }
    };

    private ModChiselModeGroups()
    {
        throw new IllegalStateException("Can not instantiate an instance of: ModChiselModeGroups. This is a utility class");
    }

    public static void onModConstruction()
    {
        LOGGER.info("Loaded chisel mode group configuration.");
    }

}
