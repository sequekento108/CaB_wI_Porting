package mod.chiselsandbits.api.client.model.baked.cache;

import net.minecraft.client.resources.model.BakedModel;

public interface IBakedModelCacheKeyCalculator<T extends BakedModel> {

    /**
     * Calculate the cache key for the given model.
     *
     * @param model the model to calculate the key for.
     * @param randomSeed the random seed to use.
     * @return the key.
     */
    IBakedModelCacheKey calculate(final T model, long randomSeed);
}
