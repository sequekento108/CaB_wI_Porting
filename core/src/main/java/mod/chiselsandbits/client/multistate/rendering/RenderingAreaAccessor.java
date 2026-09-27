package mod.chiselsandbits.client.multistate.rendering;

import mod.chiselsandbits.api.blockinformation.BlockInformation;
import mod.chiselsandbits.api.multistate.accessor.IAreaAccessor;
import mod.chiselsandbits.api.multistate.accessor.IStateEntryInfo;
import mod.chiselsandbits.api.multistate.accessor.identifier.IAreaShapeIdentifier;
import mod.chiselsandbits.api.multistate.accessor.sortable.IPositionMutator;
import mod.chiselsandbits.api.multistate.snapshot.IMultiStateSnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RenderingAreaAccessor implements IAreaAccessor {

    private final BlockInformation filter;
    private final IAreaAccessor wrapped;

    public RenderingAreaAccessor(BlockInformation filter, IAreaAccessor wrapped) {
        this.filter = filter;
        this.wrapped = wrapped;
    }
    
    @Override
    public @NotNull AABB getBoundingBox() {
        return wrapped.getBoundingBox();
    }

    @Override
    public IAreaShapeIdentifier createNewShapeIdentifier() {
        return new AreaShapeIdentifier(filter, wrapped.createNewShapeIdentifier());
    }

    @Override
    public Stream<IStateEntryInfo> stream() {
        return wrapped.stream().filter(s -> s.getBlockInformation().equals(filter));
    }

    @Override
    public boolean isInside(Vec3 inAreaTarget) {
        return wrapped.isInside(inAreaTarget);
    }

    @Override
    public boolean isInside(BlockPos inAreaBlockPosOffset, Vec3 inBlockTarget) {
        return wrapped.isInside(inAreaBlockPosOffset, inBlockTarget);
    }

    @Override
    public IMultiStateSnapshot createSnapshot() {
        throw new NotImplementedException();
    }

    @Override
    public Stream<IStateEntryInfo> streamWithPositionMutator(IPositionMutator positionMutator) {
        return wrapped.streamWithPositionMutator(positionMutator).filter(s -> s.getBlockInformation().equals(this.filter));
    }

    @Override
    public void forEachWithPositionMutator(IPositionMutator positionMutator, Consumer<IStateEntryInfo> consumer) {
        //noinspection Convert2Lambda Hot code path, an anon class performance way better here.
        wrapped.forEachWithPositionMutator(positionMutator, new Consumer<IStateEntryInfo>() {
            @Override
            public void accept(IStateEntryInfo iStateEntryInfo) {
                if (iStateEntryInfo.getBlockInformation().equals(filter))
                    consumer.accept(iStateEntryInfo);
            }
        });
    }

    @Override
    public Optional<IStateEntryInfo> getInAreaTarget(Vec3 inAreaTarget) {
        return wrapped.getInAreaTarget(inAreaTarget).filter(s -> s.getBlockInformation().equals(this.filter));
    }

    @Override
    public Optional<IStateEntryInfo> getInBlockTarget(BlockPos inAreaBlockPosOffset, Vec3 inBlockTarget) {
        return wrapped.getInBlockTarget(inAreaBlockPosOffset, inBlockTarget).filter(s -> s.getBlockInformation().equals(this.filter));
    }

    public static final class AreaShapeIdentifier implements IAreaShapeIdentifier {

        private final BlockInformation filter;
        private final IAreaShapeIdentifier wrapped;

        public AreaShapeIdentifier(BlockInformation filter, IAreaShapeIdentifier wrapped) {
            this.filter = filter;
            this.wrapped = wrapped;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AreaShapeIdentifier that = (AreaShapeIdentifier) o;
            return Objects.equals(filter, that.filter) && Objects.equals(wrapped, that.wrapped);
        }

        @Override
        public int hashCode() {
            return Objects.hash(filter, wrapped);
        }
    }
}
