package com.gursztof.pulsar.macro;

import net.minecraft.client.MinecraftClient;

public class Hitting {
    // There is small chance that mc won't be init yet so it might cause the crash
    private static final MinecraftClient minecraftInstance = MinecraftClient.getInstance();

    public static void start() {
        minecraftInstance.options.attackKey.setPressed(true);
    }

    public static void stop() {
        minecraftInstance.options.attackKey.setPressed(false);
    }
}
