package mod.chiselsandbits.client.logic;

import com.communi.suggestu.scena.core.dist.Dist;
import com.communi.suggestu.scena.core.dist.DistExecutor;
import mod.chiselsandbits.ChiselsAndBits;
import mod.chiselsandbits.item.MeasuringTapeItem;
import mod.chiselsandbits.keys.KeyBindingManager;
import mod.chiselsandbits.network.packets.MeasurementsResetPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class MeasurementTapeItemResetHandler {

    public static void checkAndDoReset() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (KeyBindingManager.getInstance().isResetMeasuringTapeKeyPressed()) {
                ItemStack stack = ItemStack.EMPTY;
                if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof MeasuringTapeItem) {
                    stack = Minecraft.getInstance().player.getMainHandItem();
                }
                else if (Minecraft.getInstance().player.getOffhandItem().getItem() instanceof MeasuringTapeItem) {
                    stack = Minecraft.getInstance().player.getOffhandItem();
                }

                if (!stack.isEmpty() && stack.getItem() instanceof MeasuringTapeItem measuringTapeItem) {
                    measuringTapeItem.clear(stack);
                    ChiselsAndBits.getInstance().getNetworkChannel().sendToServer(new MeasurementsResetPacket());
                }
            }
        });
    }
}