package com.gursztof.pulsar.macro;

import net.minecraft.client.MinecraftClient;

public class Movement {
    // There is small chance that mc won't be init yet so it might cause the crash
    private static final MinecraftClient minecraftInstance = MinecraftClient.getInstance();



    // TODO implement way to have movement forward and left at the same time
    public static void goLeft() {
        resetMove();
        minecraftInstance.options.leftKey.setPressed(true);
    }

    public static void goRight() {
        resetMove();
        minecraftInstance.options.rightKey.setPressed(true);
    }

    public static void goForward() {
        resetMove();
        minecraftInstance.options.sprintKey.setPressed(true);
        minecraftInstance.options.forwardKey.setPressed(true);
    }

    public static void resetMove() {
        minecraftInstance.options.sprintKey.setPressed(false);
        minecraftInstance.options.leftKey.setPressed(false);
        minecraftInstance.options.rightKey.setPressed(false);
        minecraftInstance.options.forwardKey.setPressed(false);
    }

}
