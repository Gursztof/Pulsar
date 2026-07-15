package com.gursztof.pulsar.macro;

import net.minecraft.client.MinecraftClient;

public class Movement {
    public static void goLeft() {
        resetMove();
        MinecraftClient.getInstance().options.leftKey.setPressed(true);
    }

    public static void goRight() {
        resetMove();
        MinecraftClient.getInstance().options.rightKey.setPressed(true);
    }

    public static void goForward() {
        resetMove();
        MinecraftClient.getInstance().options.sprintKey.setPressed(true);
        MinecraftClient.getInstance().options.forwardKey.setPressed(true);
    }

    public static void resetMove() {
        MinecraftClient.getInstance().options.leftKey.setPressed(false);
        MinecraftClient.getInstance().options.rightKey.setPressed(false);
        MinecraftClient.getInstance().options.forwardKey.setPressed(false);
    }

}
