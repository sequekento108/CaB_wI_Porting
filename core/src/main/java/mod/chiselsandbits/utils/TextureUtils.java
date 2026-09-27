package mod.chiselsandbits.utils;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

public class TextureUtils
{

    private TextureUtils()
    {
        throw new IllegalStateException("Can not instantiate an instance of: TextureUtils. This is a utility class");
    }

    /**
     * Sets up the rendering engine to render a texture out to CPU memory and prepare it for writing to disk.
     * The {@link NativeImage} returned here is still connected to the GPU and needs to be closed when done.
     *
     * @param imageName The texture name to write to the NativeImage.
     * @return The {@link NativeImage} with the image of the given texture contained.
     */
    public static NativeImage getNativeImageFromTexture(final ResourceLocation imageName) {
        final AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(imageName);
        final int openGlTextureId = texture.getId();

        Minecraft.getInstance().getTextureManager().bindForSetup(imageName);

        int format = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_INTERNAL_FORMAT);
        int width = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
        int height = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);


        final NativeImage nativeImage = new NativeImage(
          format == GL_RGB ? NativeImage.Format.RGB : NativeImage.Format.RGBA,
          width,
          height,
          false
        );

        nativeImage.downloadTexture(0, true);
        return nativeImage;
    }
}
