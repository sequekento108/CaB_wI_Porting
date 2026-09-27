package mod.chiselsandbits.client.model.baked.bit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.item.bit.IBitItem;
import mod.chiselsandbits.api.variant.state.IStateVariantManager;
import mod.chiselsandbits.client.model.baked.simple.NullBakedModel;
import mod.chiselsandbits.client.time.TickHandler;
import mod.chiselsandbits.registrars.ModCreativeTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BitBlockBakedModelManager
{
    private static final Logger                         LOGGER            = LogManager.getLogger();
    private static final BitBlockBakedModelManager      INSTANCE          = new BitBlockBakedModelManager();
    private final        Cache<BlockInformation, BakedModel> modelCache        = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
    private final        Cache<BlockInformation, BakedModel> largeModelCache   = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
    private final        NonNullList<ItemStack>         alternativeStacks = NonNullList.create();

    private BitBlockBakedModelManager()
    {
    }

    public static BitBlockBakedModelManager getInstance()
    {
        return INSTANCE;
    }

    public void clearCache() {
        modelCache.asMap().clear();
        largeModelCache.asMap().clear();
    }

    public BakedModel get(
      ItemStack stack,
      final Level world,
      final LivingEntity entity)
    {
        return get(
          stack,
          world,
          entity,
                !Minecraft.getInstance().options.keyShift.isUnbound() && Minecraft.getInstance().options.keyShift.isDown() || Screen.hasShiftDown()
        );
    }

    public BakedModel get(
      ItemStack stack,
      final Level world,
      final LivingEntity entity,
      final boolean large
    )
    {
        if (!(stack.getItem() instanceof IBitItem))
        {
            LOGGER.warn("Tried to get bit item model for non bit item");
            return NullBakedModel.instance;
        }

        final BakedModel model = get(
          large,
          ((IBitItem) stack.getItem()).getBlockInformation(stack),
          world,
          entity
        );

        return model.getOverrides().resolve(
          model,
          stack,
          null,
          null,
          0
        );
    }

    public BakedModel get(
      final boolean large,
      @Nullable BlockInformation blockInformation,
      Level level,
      final LivingEntity entity)
    {
        if (level == null) {
            level = Minecraft.getInstance().level;
            
            if (level == null)
                return NullBakedModel.instance;
        }
        
        if (blockInformation == null || blockInformation.isAir())
        {
            if (alternativeStacks.isEmpty()) {
                ModCreativeTabs.BITS.get().buildContents(new CreativeModeTab.ItemDisplayParameters(FeatureFlags.VANILLA_SET, false, level.registryAccess()));
                this.alternativeStacks.addAll(ModCreativeTabs.BITS.get().getDisplayItems());
            }

            final int alternativeIndex = (int) ((Math.floor(TickHandler.getClientTicks() / 20d)) % alternativeStacks.size());

            final ItemStack alternativeStack = this.alternativeStacks.get(alternativeIndex);
            if (!(alternativeStack.getItem() instanceof IBitItem))
            {
                throw new IllegalStateException("BitItem returned none bit item stack!");
            }

            blockInformation = ((IBitItem) alternativeStack.getItem()).getBlockInformation(alternativeStack);
        }

        final Cache<BlockInformation, BakedModel> target = large ? largeModelCache : modelCache;
        final BlockInformation workingState = blockInformation;
        try
        {
            Level finalLevel = level;
            return target.get(blockInformation, () -> {
                if (large)
                {
                    ItemStack lookupStack = IStateVariantManager.getInstance().getItemStack(workingState).orElseGet(
                      () -> new ItemStack(workingState.blockState().getBlock())
                    );
                    if (workingState.blockState().getBlock() instanceof LiquidBlock)
                    {
                        lookupStack = new ItemStack(workingState.blockState().getFluidState().getType().getBucket());
                    }
                    return Minecraft.getInstance().getItemRenderer().getModel(
                      lookupStack,
                      finalLevel,
                      entity,
                      0
                    );
                }
                else
                {
                    return new BitBlockBakedModel(workingState);
                }
            });
        }
        catch (ExecutionException e)
        {
            LOGGER.warn("Failed to get a model for a bit: " + blockInformation + " the model calculation got aborted.", e);
            return NullBakedModel.instance;
        }
    }
}
