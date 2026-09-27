package mod.chiselsandbits.client.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.chiselsandbits.api.client.icon.IIconManager;
import mod.chiselsandbits.api.util.constants.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

import static mod.chiselsandbits.client.icon.IconSpriteUploader.TEXTURE_MAP_NAME;

public class IconManager implements IIconManager
{
    private static final IconManager INSTANCE = new IconManager();

    private static final ResourceLocation ICON_SWAP = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "swap");
    private static final ResourceLocation ICON_PLACE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "place");
    private static final ResourceLocation ICON_UNDO = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "undo");
    private static final ResourceLocation ICON_REDO = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "redo");
    private static final ResourceLocation ICON_TRASH = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "trash");
    private static final ResourceLocation ICON_SORT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "sort");
    private static final ResourceLocation ICON_ROLL_X = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "roll_x");
    private static final ResourceLocation ICON_ROLL_Z = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "roll_z");
    private static final ResourceLocation ICON_WHITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "white");

    public static IconManager getInstance()
    {
        return INSTANCE;
    }

    private IconSpriteUploader iconSpriteUploader = null;

    private IconManager()
    {
    }

    public void initialize() {
        this.iconSpriteUploader = new IconSpriteUploader();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        if (resourceManager instanceof ReloadableResourceManager reloadableResourceManager) {
            reloadableResourceManager.registerReloadListener(iconSpriteUploader);
        }
    }

    @Override
    public TextureAtlasSprite getIcon(final ResourceLocation name) {
        if (this.iconSpriteUploader == null)
            throw new IllegalStateException("Tried to get icon too early.");

        return this.iconSpriteUploader.getSprite(name);
    }

    @Override
    public TextureAtlasSprite getSwapIcon() {
        return getIcon(ICON_SWAP);
    }

    @Override
    public TextureAtlasSprite getPlaceIcon() {
        return getIcon(ICON_PLACE);
    }

    @Override
    public TextureAtlasSprite getUndoIcon() {
        return getIcon(ICON_UNDO);
    }

    @Override
    public TextureAtlasSprite getRedoIcon() {
        return getIcon(ICON_REDO);
    }

    @Override
    public TextureAtlasSprite getTrashIcon() {
        return getIcon(ICON_TRASH);
    }

    @Override
    public TextureAtlasSprite getSortIcon() {
        return getIcon(ICON_SORT);
    }

    @Override
    public TextureAtlasSprite getRollXIcon() {
        return getIcon(ICON_ROLL_X);
    }

    @Override
    public TextureAtlasSprite getRollZIcon() {
        return getIcon(ICON_ROLL_Z);
    }

    @Override
    public TextureAtlasSprite getWhiteIcon() {
        return getIcon(ICON_WHITE);
    }

    @Override
    public void bindTexture()
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_MAP_NAME);
    }
}
