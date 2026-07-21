package com.gursztof.pulsar.macro;

import com.gursztof.pulsar.macroDirection.PathDirection;
import net.minecraft.client.MinecraftClient;

public class Movement {
    // There is small chance that mc won't be init yet so it might cause the crash
    private static final MinecraftClient minecraftInstance = MinecraftClient.getInstance();

    public static void go(PathDirection pathDirection) {
        switch (pathDirection) {
            case LEFT -> goLeft();
            case RIGHT -> goRight();
            case FORWARD -> goForward();
            case NOWAY -> resetMove();
        }
    }

    public static void stop() {
        resetMove();
    }

    // TODO implement way to have movement forward and left at the same time
    private static void goLeft() {
        resetMove();
        minecraftInstance.options.leftKey.setPressed(true);
    }

    private static void goRight() {
        resetMove();
        minecraftInstance.options.rightKey.setPressed(true);
    }

    private static void goForward() {
        resetMove();
        minecraftInstance.options.sprintKey.setPressed(true);
        minecraftInstance.options.forwardKey.setPressed(true);
    }

    private static void resetMove() {
        minecraftInstance.options.sprintKey.setPressed(false);
        minecraftInstance.options.leftKey.setPressed(false);
        minecraftInstance.options.rightKey.setPressed(false);
        minecraftInstance.options.forwardKey.setPressed(false);
    }

}
