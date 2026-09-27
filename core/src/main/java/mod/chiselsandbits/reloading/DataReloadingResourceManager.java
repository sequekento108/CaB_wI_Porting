package mod.chiselsandbits.reloading;

import com.google.common.collect.Sets;
import mod.chiselsandbits.aabb.AABBManager;
import mod.chiselsandbits.api.reloading.ICacheClearingHandler;
import mod.chiselsandbits.change.ChangeTrackerManger;
import mod.chiselsandbits.chiseling.LocalChiselingContextCache;
import mod.chiselsandbits.client.model.baked.chiseled.ChiseledBlockBakedModelManager;
import mod.chiselsandbits.client.model.baked.face.FaceManager;
import mod.chiselsandbits.voxelshape.VoxelShapeManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DataReloadingResourceManager implements PreparableReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DataReloadingResourceManager INSTANCE = new DataReloadingResourceManager();

    public static DataReloadingResourceManager getInstance() {
        return INSTANCE;
    }

    private final Set<ICacheClearingHandler> cacheClearingHandlers = Sets.newConcurrentHashSet();

    private DataReloadingResourceManager() {
    }

    public DataReloadingResourceManager registerCacheClearer(final ICacheClearingHandler cacheClearingHandler) {
        this.cacheClearingHandlers.add(cacheClearingHandler);
        return this;
    }

    public void onResourceManagerReload() {
        clearCaches();
    }

    public void setup() {
        this.cacheClearingHandlers.clear();

        registerCacheClearer(AABBManager.getInstance()::clearCache)
                .registerCacheClearer(VoxelShapeManager.getInstance()::clearCache)
                .registerCacheClearer(LocalChiselingContextCache.getInstance()::clearCache)
                .registerCacheClearer(ChangeTrackerManger.getInstance()::clearCache);
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(
            final PreparationBarrier barrier,
            final @NotNull ResourceManager manager,
            final @NotNull ProfilerFiller preparationProfiler,
            final @NotNull ProfilerFiller reloadProfiler,
            final @NotNull Executor backgroundExecutor,
            final @NotNull Executor gameExecutor) {
        return barrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
            reloadProfiler.startTick();
            reloadProfiler.push("C&B Data reload");
            this.onResourceManagerReload();
            reloadProfiler.pop();
            reloadProfiler.endTick();
        }, gameExecutor);
    }

    public void clearCaches() {
        LOGGER.info("Resetting common caches");
        this.cacheClearingHandlers.forEach(ICacheClearingHandler::clear);
    }
}
