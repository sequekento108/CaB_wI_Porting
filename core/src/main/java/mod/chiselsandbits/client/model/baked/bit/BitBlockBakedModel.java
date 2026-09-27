package mod.chiselsandbits.client.model.baked.bit;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.models.baked.BlockStateAwareQuad;
import com.communi.suggestu.scena.core.client.models.baked.IDataAwareBakedModel;
import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.item.bit.IBitItem;
import mod.chiselsandbits.client.model.baked.base.BaseBakedPerspectiveSmartModel;
import mod.chiselsandbits.client.util.BlockInformationUtils;
import mod.chiselsandbits.client.util.QuadGenerationUtils;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class  BitBlockBakedModel extends BaseBakedPerspectiveSmartModel implements IDataAwareBakedModel {

    private static final float BIT_BEGIN = 4f / 16;
    public static final Vector3f FROM = new Vector3f(BIT_BEGIN, BIT_BEGIN, BIT_BEGIN);
    private static final float BIT_END   = 12f / 16;
    public static final Vector3f TO = new Vector3f(BIT_END, BIT_END, BIT_END);

    final List<BakedQuad> generic = new ArrayList<>(6);

    public BitBlockBakedModel(
      final BlockInformation blockInformation)
    {
        for (final Direction myFace : Direction.values())
        {
            for (final RenderType layer : BlockInformationUtils.extractRenderTypes(blockInformation))
            {
                QuadGenerationUtils.generateQuads(
                        generic,
                        0,
                        layer,
                        blockInformation,
                        myFace,
                        myFace.getAxisDirection() == Direction.AxisDirection.POSITIVE ? TO : FROM,
                        myFace.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? TO : FROM,
                        (modelLayer, quad) -> {
                            if (modelLayer.sourceQuad() instanceof BlockStateAwareQuad blockStateAwareQuad) {
                                return new BlockStateAwareQuad(
                                        quad,
                                        blockStateAwareQuad.getBlockState()
                                );
                            }

                            return new BlockStateAwareQuad(quad, blockInformation.blockState());
                        }
                );
            }
        }
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction side, @NotNull final RandomSource rand)
    {
        if (side != null)
        {
            return Collections.emptyList();
        }

        return generic;
    }

    @Override
    public boolean usesBlockLight()
    {
        return true;
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(MissingTextureAtlasSprite.getLocation());
    }

    @Override
    public @NotNull Collection<RenderType> getSupportedRenderTypes(ItemStack itemStack, boolean b) {
        final BlockInformation blockInformation = ((IBitItem) itemStack.getItem()).getBlockInformation(itemStack);

        if (blockInformation.isFluid())
            return BlockInformationUtils.extractRenderTypes(blockInformation);

        return IRenderTypeManager.getInstance().getRenderTypesFor(
                this,
                itemStack,
                b
        );
    }

    @Override
    public BakedModel handleItemStack(ItemStack stack) {
        return this;
    }
}
