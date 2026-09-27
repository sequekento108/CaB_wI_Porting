package mod.chiselsandbits.api.client.model.baked.cache;

/**
 * Marker interface for a cache key for a baked model.
 */
public interface IBakedModelCacheKey {

    record IntBased(int value) implements IBakedModelCacheKey {
    }
}
