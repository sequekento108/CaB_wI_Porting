package mod.chiselsandbits.item;

import com.communi.suggestu.scena.core.dist.DistExecutor;
import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.chiseling.eligibility.IEligibilityAnalysisResult;
import mod.chiselsandbits.api.chiseling.eligibility.IEligibilityManager;
import mod.chiselsandbits.api.item.named.IPermanentlyHighlightedNameItem;
import mod.chiselsandbits.api.util.HelpTextUtils;
import mod.chiselsandbits.api.util.LocalStrings;
import mod.chiselsandbits.stateinfo.additional.StateVariantManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class MagnifyingGlassItem extends Item implements IPermanentlyHighlightedNameItem
{

    public MagnifyingGlassItem(Properties properties)
    {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull Component getName(final @NotNull ItemStack stack)
    {
        return DistExecutor.unsafeRunForDist(
                () -> () -> {
                    if (Minecraft.getInstance().level == null)
                    {
                        return super.getName(stack);
                    }

                    if (Minecraft.getInstance().hitResult == null)
                    {
                        return super.getName(stack);
                    }

                    if (Minecraft.getInstance().hitResult.getType() != HitResult.Type.BLOCK)
                    {
                        return super.getName(stack);
                    }

                    final BlockHitResult rayTraceResult = (BlockHitResult) Minecraft.getInstance().hitResult;
                    final BlockState state = Minecraft.getInstance().level.getBlockState(rayTraceResult.getBlockPos());

                    final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(rayTraceResult.getBlockPos());
                    final BlockInformation blockInformation = new BlockInformation(
                            state,
                            StateVariantManager.getInstance().getStateVariant(state, Optional.ofNullable(blockEntity))
                    );

                    final IEligibilityAnalysisResult result = IEligibilityManager.getInstance().analyse(blockInformation);
                    return result.isAlreadyChiseled() || result.canBeChiseled() ?
                            result.getReason().withStyle(ChatFormatting.GREEN) :
                            result.getReason().withStyle(ChatFormatting.RED);
                },
                () -> () -> super.getName(stack)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag advanced) {
        super.appendHoverText(stack, context, tooltip, advanced);
        HelpTextUtils.build(
          LocalStrings.HelpMagnifyingGlass, tooltip
        );
    }
}
