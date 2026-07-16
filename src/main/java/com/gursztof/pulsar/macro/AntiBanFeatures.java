package com.gursztof.pulsar.macro;

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.chat.ChatPrefix;
import com.gursztof.pulsar.settings.Settings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AntiBanFeatures {
    private static boolean rotated = false;

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

            // TODO if i rotate manually and then on the macro it will not work cuz the variable stays true
            if (Puslar.farmingMacro && !player.getHorizontalFacing().equals(Settings.direction) && !rotated) {
                rotated = true;
                player.sendMessage(ChatPrefix.WARNING.getPrefix().append("ROTATED"), false);
                requestDelay(Settings.maxDelay);
            }
        });
    }

    public static void shiftManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            currentTick++;

            if (client.player == null) return;
            if (currentTick >= maxTicks) {
                client.player.sendMessage(Text.literal(ChatPrefix.WARNING.getPrefix() + "Reached time limit, killing process for safety reasons"), false);
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
                player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Brake for " + brakeDurationTicks + " ticks"), false);
            }

            if (brakePending && !onBrakeBlock) {
                brakePending = false;
                isOnBrake = true;
                endBrakeTick = currentTick + brakeDurationTicks;
                Movement.resetMove();
                player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Brake started"), false);
            }

            if (isOnBrake && currentTick >= endBrakeTick) {
                isOnBrake = false;
                player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Brake ended"), false);
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
                dilayRequest = false;
            }

            if (isOnDelay) {
                if (FarmingMacro.lastTurnWasLeft) {
                    Movement.goRight();
                } else {
                    Movement.goLeft();
                }
                client.player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Dilay for " + dilayTicks + " ticks"), false);
            }

            if (!isOnDelay && dilayTicks > 1) {
                client.player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Dilay stopped"), false);
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
