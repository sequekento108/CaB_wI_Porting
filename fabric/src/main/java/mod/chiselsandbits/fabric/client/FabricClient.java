package mod.chiselsandbits.fabric.client;

import mod.chiselsandbits.client.ChiselsAndBitsClient;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("chisels-and-bits-fabric");

    private ChiselsAndBitsClient chiselsAndBits;

    public FabricClient()
    {
    }

    @Override
    public void onInitializeClient()
    {
        LOGGER.info("Initialized chisels-and-bits for Fabric client module");
        setChiselsAndBits(new ChiselsAndBitsClient());
    }

    public void setChiselsAndBits(final ChiselsAndBitsClient chiselsAndBits)
    {
        this.chiselsAndBits = chiselsAndBits;
    }
}
