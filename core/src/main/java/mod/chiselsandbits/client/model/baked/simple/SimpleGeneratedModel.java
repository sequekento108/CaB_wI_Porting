package mod.chiselsandbits.client.model.baked.simple;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.client.model.baked.BakedQuadBuilder;
import mod.chiselsandbits.utils.LightUtil;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleGeneratedModel implements BakedModel
{

    @SuppressWarnings("unchecked")
    private final List<BakedQuad>[] face = new List[6];

    private final TextureAtlasSprite texture;

    public SimpleGeneratedModel(
      final TextureAtlasSprite texture )
    {
        // create lists...
        face[0] = new ArrayList<>();
        face[1] = new ArrayList<>();
        face[2] = new ArrayList<>();
        face[3] = new ArrayList<>();
        face[4] = new ArrayList<>();
        face[5] = new ArrayList<>();

        this.texture = texture;

        final float[] afloat = new float[] { 0, 0, 16, 16 };
        final BlockFaceUV uv = new BlockFaceUV( afloat, 0 );
        final FaceBakery faceBakery = new FaceBakery();

        final Vector3f to = new Vector3f( 0.0f, 0.0f, 0.0f );
        final Vector3f from = new Vector3f( 16.0f, 16.0f, 16.0f );

        final BlockModelRotation mr = BlockModelRotation.X0_Y0;

        for ( final Direction side : Direction.values() )
        {
            final BlockElementFace bpf = new BlockElementFace( side, 1, "", uv );

            Vector3f toB, fromB;

            switch (side)
            {
                case UP -> {
                    toB = new Vector3f(to.x(), from.y(), to.z());
                    fromB = new Vector3f(from.x(), from.y(), from.z());
                }
                case EAST -> {
                    toB = new Vector3f(from.x(), to.y(), to.z());
                    fromB = new Vector3f(from.x(), from.y(), from.z());
                }
                case NORTH -> {
                    toB = new Vector3f(to.x(), to.y(), to.z());
                    fromB = new Vector3f(from.x(), from.y(), to.z());
                }
                case SOUTH -> {
                    toB = new Vector3f(to.x(), to.y(), from.z());
                    fromB = new Vector3f(from.x(), from.y(), from.z());
                }
                case DOWN -> {
                    toB = new Vector3f(to.x(), to.y(), to.z());
                    fromB = new Vector3f(from.x(), to.y(), from.z());
                }
                case WEST -> {
                    toB = new Vector3f(to.x(), to.y(), to.z());
                    fromB = new Vector3f(to.x(), from.y(), from.z());
                }
                default -> throw new NullPointerException();
            }

            final BakedQuad g = faceBakery.bakeQuad( toB, fromB, bpf, texture, side, mr, null, false);
            face[side.ordinal()].add( finishFace( g, side) );
        }
    }

    private BakedQuad finishFace(
      final BakedQuad g,
      final Direction myFace)
    {
        final int[] vertData = g.getVertices();
        final int wrapAt = vertData.length / 4;

        final BakedQuadBuilder builder = new BakedQuadBuilder(g.getSprite());
        builder.setQuadOrientation( myFace );
        builder.setQuadTint( 1 );

        for ( int vertNum = 0; vertNum < 4; vertNum++ )
        {
            for ( int elementIndex = 0; elementIndex < DefaultVertexFormat.BLOCK.getElements().size(); elementIndex++ )
            {
                final VertexFormatElement element = DefaultVertexFormat.BLOCK.getElements().get(elementIndex);
                switch ( element.usage() )
                {
                    case POSITION:
                        builder.put(vertNum, elementIndex, Float.intBitsToFloat( vertData[wrapAt * vertNum] ), Float.intBitsToFloat( vertData[1 + wrapAt * vertNum] ), Float.intBitsToFloat( vertData[2 + wrapAt * vertNum] ) );
                        break;

                    case COLOR:
                        final float light = LightUtil.diffuseLight( myFace );
                        builder.put(vertNum, elementIndex, light, light, light, 1f );
                        break;

                    case NORMAL:
                        builder.put(vertNum, elementIndex, myFace.getStepX(), myFace.getStepY(), myFace.getStepZ() );
                        break;

                    case UV:

                        if ( element.index() == 1 )
                        {
                            builder.put(vertNum, elementIndex, 0, 0 );
                        }
                        else
                        {
                            final float u = Float.intBitsToFloat( vertData[4 + wrapAt * vertNum] );
                            final float v = Float.intBitsToFloat( vertData[5 + wrapAt * vertNum] );
                            builder.put(vertNum, elementIndex, u, v );
                        }

                        break;

                    default:
                        builder.put(vertNum, elementIndex );
                        break;
                }
            }
        }

        builder.onComplete();

        return builder.build();
    }

    public List<BakedQuad>[] getFace()
    {
        return face;
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(
      final BlockState state,
      final Direction side,
      @NotNull final RandomSource rand )
    {
        if ( side == null )
        {
            return Collections.emptyList();
        }

        return face[side.ordinal()];
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    @Override
    public boolean usesBlockLight()
    {
        return false;
    }

    @NotNull
    @Override
    public ItemTransforms getTransforms()
    {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return texture;
    }

    @Override
    public boolean isCustomRenderer()
    {
        return false;
    }

    @NotNull
    @Override
    public ItemOverrides getOverrides()
    {
        return ItemOverrides.EMPTY;
    }

}
