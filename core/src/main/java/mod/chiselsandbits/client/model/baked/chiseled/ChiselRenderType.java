package mod.chiselsandbits.client.model.baked.chiseled;

import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.multistate.accessor.IAreaAccessor;
import mod.chiselsandbits.api.multistate.accessor.IStateEntryInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

import java.security.InvalidParameterException;
import java.util.Collection;

public enum ChiselRenderType
{
    SOLID( RenderType.solid(), RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS), VoxelType.SOLID ),
    SOLID_FLUID( RenderType.solid(), RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS), VoxelType.FLUID),
    CUTOUT( RenderType.cutout(), RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS), VoxelType.UNKNOWN),
    CUTOUT_MIPPED( RenderType.cutoutMipped(), RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS), VoxelType.UNKNOWN),
    TRANSLUCENT( RenderType.translucent(), RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), VoxelType.UNKNOWN),
    TRANSLUCENT_FLUID( RenderType.translucent(), RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), VoxelType.FLUID),
    TRIPWIRE (RenderType.tripwire(), RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), VoxelType.UNKNOWN);

    public final RenderType layer;
    public final RenderType entityLayer;
    public final VoxelType type;

    private static final Multimap<VoxelType, ChiselRenderType> TYPED_RENDER_TYPES = HashMultimap.create();
    static {
        for (final ChiselRenderType value : values())
        {
            TYPED_RENDER_TYPES.put(value.type, value);
        }
    }

    ChiselRenderType(
            final RenderType layer, RenderType entityLayer,
            final VoxelType type)
    {
        this.layer = layer;
        this.entityLayer = entityLayer;
        this.type = type;
    }

    public boolean has(RenderType type) {
        return layer.equals(type) || entityLayer.equals(type);
    }

    public boolean isRequiredForRendering(
      final IAreaAccessor accessor )
    {
        if ( accessor == null )
        {
            return false;
        }

        return accessor.stream()
          .anyMatch(this::isRequiredForRendering);
    }

    public boolean isRequiredForRendering(
      final IStateEntryInfo stateEntryInfo )
    {
        return isRequiredForRendering(stateEntryInfo.getBlockInformation());
    }

    public boolean isRequiredForRendering(
      final BlockInformation state )
    {
        if (state.isAir() || !this.type.isValidBlockState(state))
            return false;

        if (this.type.isFluid()) {
            return IRenderTypeManager.getInstance().canRenderInType(state.blockState().getFluidState(), this.layer);
        }

        return IRenderTypeManager.getInstance().canRenderInType(state.blockState(), this.layer);
    }

    public static ChiselRenderType fromLayer(
      RenderType layerInfo,
      final boolean isFluid )
    {
        if (layerInfo == null)
            layerInfo = RenderType.solid();

        if (ChiselRenderType.CUTOUT.has(layerInfo))
        {
            return CUTOUT;
        }
        else if (ChiselRenderType.CUTOUT_MIPPED.has(layerInfo))
        {
            return CUTOUT_MIPPED;
        }
        else if (ChiselRenderType.SOLID.has(layerInfo))
        {
            return isFluid ? SOLID_FLUID : SOLID;
        }
        else if (ChiselRenderType.TRANSLUCENT.has(layerInfo))
        {
            return isFluid ? TRANSLUCENT_FLUID : TRANSLUCENT;
        }
        else if (ChiselRenderType.TRIPWIRE.has(layerInfo))
        {
            return TRIPWIRE;
        }

        throw new InvalidParameterException();
    }

    public static Collection<ChiselRenderType> getRenderTypes(final VoxelType voxelType) {
        return TYPED_RENDER_TYPES.get(voxelType);
    }
}
