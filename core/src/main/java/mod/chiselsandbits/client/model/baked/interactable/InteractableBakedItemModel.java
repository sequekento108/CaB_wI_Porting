package mod.chiselsandbits.client.model.baked.interactable;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InteractableBakedItemModel implements BakedModel
{

    private final BakedModel innerModel;

    public InteractableBakedItemModel(final BakedModel innerModel) {this.innerModel = innerModel;}

    @Override
    public @NotNull List<BakedQuad> getQuads(
      @Nullable final BlockState state, @Nullable final Direction side, final @NotNull RandomSource rand)
    {
        return innerModel.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return innerModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return innerModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight()
    {
        return innerModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer()
    {
        return true;
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon()
    {
        return innerModel.getParticleIcon();
    }

    @Override
    public @NotNull ItemOverrides getOverrides()
    {
        return innerModel.getOverrides();
    }

    public BakedModel getInnerModel()
    {
        return innerModel;
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return innerModel.getTransforms();
    }
}
