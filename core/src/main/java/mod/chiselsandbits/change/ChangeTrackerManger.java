package mod.chiselsandbits.change;

import com.google.common.collect.Maps;
import mod.chiselsandbits.api.change.IChangeTracker;
import mod.chiselsandbits.api.change.IChangeTrackerManager;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class ChangeTrackerManger implements IChangeTrackerManager
{
    private static final ChangeTrackerManger INSTANCE = new ChangeTrackerManger();

    public static ChangeTrackerManger getInstance()
    {
        return INSTANCE;
    }

    private final Map<UUID, ChangeTracker> changeTrackers = Maps.newConcurrentMap();
    private final ChangeTracker clientChangeTracker = new ChangeTracker();

    private ChangeTrackerManger()
    {
    }

    @Override
    public @NotNull IChangeTracker getChangeTracker(final Player player)
    {
        if (player.level().isClientSide())
            return clientChangeTracker;

        return changeTrackers.computeIfAbsent(
          player.getUUID(),
          id -> new ChangeTracker(player)
        );
    }

    public void clearCache() {
        changeTrackers.values().forEach(ChangeTracker::reset);
        changeTrackers.clear();
    }
}
