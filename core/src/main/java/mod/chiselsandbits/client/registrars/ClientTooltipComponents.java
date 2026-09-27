package mod.chiselsandbits.client.registrars;

import mod.chiselsandbits.client.tooltip.ChiseledBlockTooltipHandler;

public class ClientTooltipComponents {

    public static void onClientConstruction() {
        ChiseledBlockTooltipHandler.configure();
    }
}
