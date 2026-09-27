package mod.chiselsandbits.client.model.baked.cache;

import mod.chiselsandbits.api.client.model.baked.cache.IBakedModelCacheKey;
import mod.chiselsandbits.api.client.model.baked.cache.IBakedModelCacheKeyCalculator;
import mod.chiselsandbits.api.client.model.baked.cache.IBakedModelCacheKeyCalculatorRegistry;
import net.minecraft.Util;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.util.RandomSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BakedModelCacheKeyCalculatorRegistry implements IBakedModelCacheKeyCalculatorRegistry {

    private static final IBakedModelCacheKeyCalculator<?> DEFAULT = (model, seed) -> new IBakedModelCacheKey.IntBased(0);

    private static final BakedModelCacheKeyCalculatorRegistry INSTANCE = new BakedModelCacheKeyCalculatorRegistry();

    public static BakedModelCacheKeyCalculatorRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<Class<? extends BakedModel>, IBakedModelCacheKeyCalculator<?>> calculatorMap = new ConcurrentHashMap<>();

    private BakedModelCacheKeyCalculatorRegistry() {
        registerFor(new WeightedBakedModelCacheKeyCalculator(), WeightedBakedModel.class);
    }

    @SafeVarargs
    @Override
    public final <T extends BakedModel> void registerFor(IBakedModelCacheKeyCalculator<T> calculator, Class<? extends T>... keyType) {
        for (final Class<? extends BakedModel> type : keyType) {
            if (calculatorMap.put(type, calculator) != null) {
                throw new IllegalArgumentException("The calculator for key type: " + type + " is already registered!");
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends BakedModel> IBakedModelCacheKey getCacheKey(final T model, long randomSeed) {
        final IBakedModelCacheKeyCalculator calculator = calculatorMap.getOrDefault(model.getClass(), DEFAULT);
        return calculator.calculate(model, randomSeed);
    }


    private static final class WeightedBakedModelCacheKeyCalculator implements IBakedModelCacheKeyCalculator<WeightedBakedModel> {

        private static final RandomSource RANDOM = Util.make(RandomSource.createNewThreadLocalInstance(), (random) -> random.setSeed(42L));

        @Override
        public IBakedModelCacheKey calculate(WeightedBakedModel model, long seed) {
            RANDOM.setSeed(seed);
            final int index = Math.abs((int)RANDOM.nextLong()) % model.totalWeight;
            return new IBakedModelCacheKey.IntBased(index);
        }
    }
}
