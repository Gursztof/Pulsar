package com.gursztof.pulsar.macro;

import com.gursztof.pulsar.Puslar;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AntiBanFeatures {
    private static int currentTick = 0;
    private static final int maxTicks = 252000;

    public static boolean isOnBrake = false;
    private static int endBrakeTick = 0;
    private static boolean brakePending = false;
    private static int brakeDurationTicks = 0;

    private static int endDelayTick = 0;
    static boolean dilayRequest = false;
    static int dilayTicks = 0;

    // TODO MACRO ADMINS CHECKS!!!
    public static void rotationCheck() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;

            if (player == null) return;

            if (Puslar.farmingMacro && !player.getHorizontalFacing().equals(FarmingMacro.direction)) {
                player.sendMessage(Text.literal("Roatated!"), false);
                requestDelay(43);
            }
        });
    }

    public static void shiftManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            currentTick++;

            if (client.player == null) return;
            if (currentTick >= maxTicks) {
                client.player.sendMessage(Text.literal("Reached time limit, killing process for safety reasons"), false);
                // TODO add kill
            }
        });
    }

    public static void brakeManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            World world = client.world;
            if (player == null || world == null) return;

            boolean onBrakeBlock = isBrakeBlock(player, world);

            if (onBrakeBlock && !brakePending && !isOnBrake) {
                brakeDurationTicks = 140 + (int) (Math.random() * (1200 - 140));
                brakePending = true;
                player.sendMessage(Text.literal("Brake: + " + brakeDurationTicks + " (ticks)"), false);
            }

            if (brakePending && !onBrakeBlock) {
                brakePending = false;
                isOnBrake = true;
                endBrakeTick = currentTick + brakeDurationTicks;
                Movement.resetMove();
                player.sendMessage(Text.literal("Brake started"), false);
            }

            if (isOnBrake && currentTick >= endBrakeTick) {
                isOnBrake = false;
                player.sendMessage(Text.literal("Brake ended"), false);
            }
        });
    }

    public static void reactionDelayManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean isOnDelay = currentTick <= endDelayTick;

            if (dilayRequest) {
                Puslar.farmingMacro = false;
                Movement.resetMove();
                client.player.sendMessage(Text.literal("Delay requested"), false);
                dilayRequest = false;
            }

            if (isOnDelay) {
                if (FarmingMacro.lastTurnWasLeft) {
                    Movement.goRight();
                } else {
                    Movement.goLeft();
                }
                client.player.sendMessage(Text.literal("On delay" + currentTick + " : " + endDelayTick), false);
            }

            if (!isOnDelay && dilayTicks > 1) {
                client.player.sendMessage(Text.literal("Delay stopped"), false);
                dilayTicks = 0;
                Movement.resetMove();
            }
        });
    }

    private static void requestDelay(int ticks) {
        dilayRequest = true;
        dilayTicks = ticks;
        endDelayTick = currentTick + ticks;
    }

    private static boolean isBrakeBlock(ClientPlayerEntity player, World world) {
        BlockPos blockPos = player.getBlockPos().down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.getBlock().equals(Blocks.COARSE_DIRT);
    }
}
