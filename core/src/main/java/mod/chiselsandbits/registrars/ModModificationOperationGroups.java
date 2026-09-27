package mod.chiselsandbits.registrars;

import mod.chiselsandbits.api.modification.operation.IModificationOperationGroup;
import mod.chiselsandbits.api.util.LocalStrings;
import mod.chiselsandbits.api.util.constants.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ModModificationOperationGroups
{
    private static final Logger                      LOGGER = LogManager.getLogger();
    public static        IModificationOperationGroup ROTATE = new IModificationOperationGroup()
    {
        @Override
        public @NotNull ResourceLocation getIcon()
        {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/rotate.png");
        }

        @Override
        public Component getDisplayName()
        {
            return LocalStrings.PatternModificationGroupRotate.getText();
        }
    };
    public static        IModificationOperationGroup MIRROR = new IModificationOperationGroup()
    {
        @Override
        public @NotNull ResourceLocation getIcon()
        {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/mirror.png");
        }

        @Override
        public Component getDisplayName()
        {
            return LocalStrings.PatternModificationGroupMirror.getText();
        }
    };

    private ModModificationOperationGroups()
    {
        throw new IllegalStateException("Can not instantiate an instance of: ModModificationOperationGroups. This is a utility class");
    }

    public static void onModConstruction()
    {
        LOGGER.info("Loaded modification operation group configuration.");
    }

}
