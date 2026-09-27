package mod.chiselsandbits.client.model.baked.face.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import mod.chiselsandbits.api.block.state.id.IBlockStateIdManager;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.client.model.baked.BakedQuadBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

public record ModelQuadLayer(VertexData[] vertexData, TextureAtlasSprite sprite, int light, int color, int tint,
                             boolean shade, Direction cullDirection, BakedQuad sourceQuad) {

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder extends BaseModelReader {
        private final BlockInformation blockInformation;
        private final Collection<VertexData> manualVertexData = new ArrayList<>();
        private final ModelLightMapReader lightValueExtractor;
        private final ModelVertexDataReader uvExtractor;
        private TextureAtlasSprite sprite;
        private int light;
        private int color = -1;
        private int tint = -1;
        private boolean shade;
        private Direction cullDirection;
        private BakedQuad sourceQuad;

        private Builder(final BlockInformation blockInformation) {
            this.lightValueExtractor = new ModelLightMapReader();
            this.uvExtractor = new ModelVertexDataReader();
            this.blockInformation = blockInformation;
        }

        public static Builder create(final BlockInformation blockInformation) {
            return new Builder(blockInformation);
        }

        public Builder withVertexData(final Consumer<VertexData.Builder> vertexDataConsumer) {
            final VertexData.Builder vertexDataBuilder = VertexData.Builder.create();
            vertexDataConsumer.accept(vertexDataBuilder);
            manualVertexData.add(vertexDataBuilder.build());
            return this;
        }

        public Builder withSprite(TextureAtlasSprite sprite) {
            this.sprite = sprite;
            return this;
        }

        public Builder withLight(int light) {
            this.light = light;
            return this;
        }

        public Builder withColor(int color) {
            this.color = color;
            return this;
        }

        public Builder withTint(int tint) {
            this.tint = tint;
            return this;
        }

        public Builder withShade(boolean shade) {
            this.shade = shade;
            return this;
        }

        public Builder withSourceQuad(BakedQuad sourceQuad) {
            this.sourceQuad = sourceQuad;
            return this;
        }

        private Builder withCullDirection(Direction orientation) {
            this.cullDirection = orientation;
            return this;
        }

        @Override
        public void put(final int vertexIndex,
                        final int element,
                        final float @NotNull ... data) {
            uvExtractor.put(vertexIndex, element, data);
            lightValueExtractor.put(vertexIndex, element, data);
        }

        @Override
        public void setQuadTint(int tint) {
            withTint(tint);
        }

        @Override
        public void setApplyDiffuseLighting(boolean diffuse) {
            withShade(diffuse);
        }

        @Override
        public void setQuadOrientation(@NotNull Direction orientation) {
            withCullDirection(orientation);
        }

        @Override
        public void setTexture(@NotNull TextureAtlasSprite texture) {
            withSprite(texture);
        }

        @Override
        public void onComplete() {
            uvExtractor.onComplete();
            lightValueExtractor.onComplete();
        }

        public ModelQuadLayer build() {
            light = Math.max(this.light, lightValueExtractor.getLv());
            color = tint != -1 ? color : 0xffffffff;

            if (0x00 <= tint && tint <= 0xff) {
                color = 0xffffffff;
                tint = (IBlockStateIdManager.getInstance().getIdFrom(blockInformation.blockState()) << 8) | tint;
            } else {
                tint = -1;
            }

            final BakedQuad sourceQuad = this.sourceQuad == null ? buildSourceQuad() : this.sourceQuad;

            final Collection<VertexData> vertexData = manualVertexData.size() != 0 ? manualVertexData.stream().sorted(Comparator.comparing(VertexData::vertexIndex)).toList() : uvExtractor.getVertexData();
            return new ModelQuadLayer(vertexData.toArray(VertexData[]::new), sprite, light, color, tint, shade, cullDirection, sourceQuad);
        }

        private BakedQuad buildSourceQuad() {
            if (manualVertexData.size() != 4) {
                throw new IllegalStateException("Cannot build a source quad without 4 vertex data");
            }

            final VertexData[] verticesData = manualVertexData.stream().sorted(Comparator.comparing(VertexData::vertexIndex)).toArray(VertexData[]::new);

            final BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
            builder.setQuadOrientation(cullDirection);
            builder.setQuadTint(tint);

            for (int vertexIndex = 0; vertexIndex < verticesData.length; vertexIndex++) {
                final VertexData vertexData = verticesData[vertexIndex];

                for (int elementIndex = 0; elementIndex < DefaultVertexFormat.BLOCK.getElements().size(); elementIndex++) {
                    final VertexFormatElement element = DefaultVertexFormat.BLOCK.getElements().get(elementIndex);
                    switch (element.usage()) {
                        case POSITION:
                            builder.put(vertexIndex, elementIndex, vertexData.positionData());
                            break;
                        case COLOR:
                            builder.put(vertexIndex, elementIndex, 1f, 1f, 1f, 1f);
                            break;
                        case NORMAL:
                            builder.put(vertexIndex, elementIndex, cullDirection.getStepX(), cullDirection.getStepY(), cullDirection.getStepZ());
                            break;
                        case UV:
                            if (element.index() == 0) {
                                builder.put(vertexIndex, elementIndex, vertexData.uvData());
                            } else if (element.index() == 1) {
                                builder.put(vertexIndex, elementIndex, 0, 0);
                            } else {
                                builder.put(vertexIndex, elementIndex, 0, 0);
                            }
                            break;
                        default:
                            builder.put(vertexIndex, elementIndex);
                            break;
                    }
                }
            }

            builder.setApplyDiffuseLighting(true);

            builder.onComplete();
            return builder.build();
        }
    }
}