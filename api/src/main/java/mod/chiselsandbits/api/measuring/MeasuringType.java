package mod.chiselsandbits.api.measuring;

import mod.chiselsandbits.api.item.withmode.group.IToolModeGroup;
import mod.chiselsandbits.api.multistate.StateEntrySize;
import mod.chiselsandbits.api.util.BlockHitResultUtils;
import mod.chiselsandbits.api.util.LocalStrings;
import mod.chiselsandbits.api.util.VectorUtils;
import mod.chiselsandbits.api.util.constants.Constants;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public enum MeasuringType implements IToolModeGroup
{
    BIT(LocalStrings.TapeMeasureBit.getText(),
      ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/bit.png"),
      blockHitResult -> BlockHitResultUtils.getCenterOfHitObject(blockHitResult, StateEntrySize.current().getSizePerBitScalingVector()), (from, to, hitFace) ->
      new Vec3(
        Math.min(from.x(), to.x()) - StateEntrySize.current().getSizePerHalfBit(),
        Math.min(from.y(), to.y()) - StateEntrySize.current().getSizePerHalfBit(),
        Math.min(from.z(), to.z()) - StateEntrySize.current().getSizePerHalfBit()
      )
    ,
      (from, to, hitFace) ->
        new Vec3(
          Math.max(from.x(), to.x()) + StateEntrySize.current().getSizePerHalfBit(),
          Math.max(from.y(), to.y()) + StateEntrySize.current().getSizePerHalfBit(),
          Math.max(from.z(), to.z()) + StateEntrySize.current().getSizePerHalfBit()
        ),
      true),
    BLOCK(LocalStrings.TapeMeasureBlock.getText(), ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/block.png"),
      blockHitResult -> BlockHitResultUtils.getCenterOfHitObject(blockHitResult, VectorUtils.ONE), (from, to, hitFace) ->
      new Vec3(
        Math.min(from.x(), to.x()) - 0.499,
        Math.min(from.y(), to.y()) - 0.499,
        Math.min(from.z(), to.z()) - 0.499
      )
    ,
      (from, to, hitFace) ->
        new Vec3(
          Math.max(from.x(), to.x()) + 0.499,
          Math.max(from.y(), to.y()) + 0.499,
          Math.max(from.z(), to.z()) + 0.499
        ),
      true),
    DISTANCE( LocalStrings.TapeMeasureDistance.getText(), ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID,"textures/icons/line.png"),
      IClickedPositionAdapter.identity(), (from, to, hitFace) -> from,
      (from, to, hitFace) -> to, false);

    private final Component displayName;
    private final ResourceLocation        icon;
    private final IClickedPositionAdapter clickedPositionAdapter;
    private final IPositionAdapter        finalStartPositionAdapter;
    private final IPositionAdapter finalEndPositionAdapter;
    private final boolean needsNormalization;

    MeasuringType(
      final Component displayName,
      final ResourceLocation icon,
      final IClickedPositionAdapter startPositionAdapter,
      IPositionAdapter finalStartPositionAdapter,
      IPositionAdapter finalEndPositionAdapter, final boolean needsNormalization) {
        this.displayName = displayName;
        this.icon = icon;
        this.clickedPositionAdapter = startPositionAdapter;
        this.finalStartPositionAdapter = finalStartPositionAdapter;
        this.finalEndPositionAdapter = finalEndPositionAdapter;
        this.needsNormalization = needsNormalization;
    }

    @Override
    public @NotNull ResourceLocation getIcon()
    {
        return icon;
    }

    @Override
    public Component getDisplayName()
    {
        return displayName;
    }

    @NotNull
    public Vec3 adaptClickedPosition(@NotNull final BlockHitResult blockHitResult) {
        return this.clickedPositionAdapter.adapt(blockHitResult);
    }

    @NotNull
    public Vec3 adaptStartCorner(@NotNull final Vec3 startPosition, @NotNull final Vec3 endPosition, @NotNull final Direction hitFace) {
        return this.finalStartPositionAdapter.adapt(startPosition, endPosition, hitFace);
    }

    @NotNull
    public Vec3 adaptEndCorner(@NotNull final Vec3 startPosition, @NotNull final Vec3 endPosition, @NotNull final Direction hitFace) {
        return this.finalEndPositionAdapter.adapt(startPosition, endPosition, hitFace);
    }

    public Vec3 getResolution()
    {
        return new Vec3(1,1,1);
    }

    public boolean isNeedsNormalization()
    {
        return needsNormalization;
    }

    @FunctionalInterface
    private interface IPositionAdapter
    {
        @NotNull
        Vec3 adapt(@NotNull final Vec3 startPosition, @NotNull final Vec3 endPosition, @NotNull final Direction hitFace);
    }

    @FunctionalInterface
    private interface IClickedPositionAdapter
    {
        static IClickedPositionAdapter identity() {
            return HitResult::getLocation;
        }

        @NotNull
        Vec3 adapt(@NotNull final BlockHitResult startPosition);
    }
}
