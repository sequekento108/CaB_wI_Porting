package mod.chiselsandbits.client.util;

import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.google.common.collect.Sets;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.client.variant.state.IClientStateVariantManager;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.ThreadSafeLegacyRandomSource;

import java.util.Set;

@SuppressWarnings("deprecation")
public final class BlockInformationUtils {

    private static final RandomSource RANDOM = new ThreadSafeLegacyRandomSource(42);

    private BlockInformationUtils() {
        throw new IllegalStateException("Can not instantiate an instance of: IMultiStateObjectStatisticsUtils. This is a utility class");
    }

    public static Set<RenderType> extractRenderTypes(BlockInformation blockInformation) {
        return extractRenderTypes(Sets.newHashSet(blockInformation), Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FABULOUS);
    }

    public static Set<RenderType> extractRenderTypes(Set<BlockInformation> blockInformation) {
        return extractRenderTypes(blockInformation, Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FABULOUS);
    }

    public static Set<RenderType> extractRenderTypes(Set<BlockInformation> blocks, boolean entity) {
        final Set<RenderType> renderTypes = Sets.newHashSet();
        for (BlockInformation blockInformation : blocks) {
            if (blockInformation.isAir())
                continue;

            if (blockInformation.isFluid()) {
                final RenderType renderType = ItemBlockRenderTypes.getRenderLayer(blockInformation.blockState().getFluidState());
                if (!entity || renderType != RenderType.translucent())
                    renderTypes.add(renderType);
                else if (renderType == RenderType.translucent())
                    renderTypes.add(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));

                continue;
            }

            final BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockInformation.blockState());
            renderTypes.addAll(
                    IRenderTypeManager.getInstance().getRenderTypesFor(
                            bakedModel,
                            blockInformation.blockState(),
                            RANDOM,
                            IClientStateVariantManager.getInstance().getBlockModelData(blockInformation)
                    )
            );
        }

        return renderTypes;
    }
}
