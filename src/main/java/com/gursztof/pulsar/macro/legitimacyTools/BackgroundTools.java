package com.gursztof.pulsar.macro.antiBanFeatures;

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.chat.ChatPrefix;
import com.gursztof.pulsar.macro.FarmingMacro;
import com.gursztof.pulsar.macro.Movement;
import com.gursztof.pulsar.macroDirection.PathDirection;
import com.gursztof.pulsar.settings.Settings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;

public class BackgroundTools {
    public static int currentTick = 1;

    private static int endDelayTick = 0;
    static boolean delayRequest = false;
    static int delayTicks = 0;

    public static void init() {
        shiftManager();
        reactionDelayManager();
    }

    // TODO its now init in pulsar so ticks are counting since game starts xdz
    private static void shiftManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            currentTick++;

            if (client.player == null) return;
            if (currentTick >= Settings.maxTicks) {
                client.disconnect(ChatPrefix.WARNING.getPrefix().append("Reached time limit, disconnect for safety reasons"));
            }
        });
    }

    public static void requestDelay(int ticks) {
        delayRequest = true;
        delayTicks = ticks;
        endDelayTick = currentTick + ticks;
    }

    private static void reactionDelayManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;

            if (player == null) return;

            boolean isOnDelay = currentTick <= endDelayTick;

            if (delayRequest) {
                Puslar.farmingMacro = false;
                Movement.stop();
                delayRequest = false;
            }


            // TODO maybe in future make it so it always go into farmland
            if (isOnDelay) {
                if (FarmingMacro.alignTo.equals(PathDirection.RIGHT)) {
                    Movement.go(PathDirection.RIGHT);
                } else {
                    Movement.go(PathDirection.LEFT);
                }

                if (Settings.debug) {
                    player.sendMessage(ChatPrefix.DEBUG.getPrefix().append("Delay ticks left: " + (endDelayTick - currentTick)), false);
                }
            }

            if (!isOnDelay && delayTicks > 1) {
                player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Delay stopped"), false);
                delayTicks = 0;
                Movement.stop();
            }
        });
    }
}
