package mod.chiselsandbits.client.model.baked.bit;

import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import mod.chiselsandbits.client.model.baked.base.BaseSmartModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DataAwareBitBlockBakedModel extends BaseSmartModel
{
    @Override
    public boolean usesBlockLight()
    {
        return true;
    }

    @Override
    public BakedModel resolve(
      final BakedModel originalModel, final ItemStack stack, final Level world, final LivingEntity entity)
    {
        return IRenderingManager.getInstance().getModelManager().adaptToPlatform(BitBlockBakedModelManager.getInstance().get(stack, world, entity));
    }
}
