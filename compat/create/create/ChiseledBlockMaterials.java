package mod.chiselsandbits.forge.compat.create;

import com.jozufozu.flywheel.api.struct.Batched;
import com.jozufozu.flywheel.api.struct.Instanced;
import com.jozufozu.flywheel.api.struct.StructType;
import com.jozufozu.flywheel.api.struct.StructWriter;
import com.jozufozu.flywheel.backend.gl.buffer.VecBuffer;
import com.jozufozu.flywheel.core.Programs;
import com.jozufozu.flywheel.core.layout.BufferLayout;
import com.jozufozu.flywheel.core.layout.CommonItems;
import com.jozufozu.flywheel.core.layout.MatrixItems;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.core.materials.model.ModelWriterUnsafe;
import com.jozufozu.flywheel.core.model.ModelTransformer;
import net.minecraft.resources.ResourceLocation;

public class ChiseledBlockMaterials {
    public static final StructType<ModelData> CHISELED_BLOCK = new ModelType();

    public static class ModelType implements Instanced<ModelData>, Batched<ModelData> {

        public static final BufferLayout FORMAT = BufferLayout.builder()
                .addItems(CommonItems.LIGHT, CommonItems.RGBA)
                .addItems(MatrixItems.MAT4, MatrixItems.MAT3)
                .build();

        @Override
        public ModelData create() {
            return new ModelData();
        }

        @Override
        public BufferLayout getLayout() {
            return FORMAT;
        }

        @Override
        public StructWriter<ModelData> getWriter(VecBuffer backing) {
            return new ModelWriterUnsafe(backing, this);
        }

        @Override
        public ResourceLocation getProgramSpec() {
            return ChiseledBlockPrograms.CHISELED_BLOCK;
        }

        @Override
        public void transform(ModelData d, ModelTransformer.Params b) {
            b.transform(d.model, d.normal)
                    .color(d.r, d.g, d.b, d.a)
                    .light(d.getPackedLight());
        }
    }
}
