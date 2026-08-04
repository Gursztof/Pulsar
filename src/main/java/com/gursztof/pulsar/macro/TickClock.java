package com.gursztof.pulsar.macro.safety;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class TickClock {
    public static int currentTick = 1;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            currentTick++;
        });
    }
}
