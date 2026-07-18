package com.gursztof.pulsar.macro.antiBanFeatures;

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.chat.ChatPrefix;
import com.gursztof.pulsar.macro.Movement;
import com.gursztof.pulsar.settings.Settings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TickTools {
    public static boolean rotated = false;

    public static boolean isOnBrake = false;
    private static int endBrakeTick = 0;
    private static boolean brakePending = false;
    private static int brakeDurationTicks = 0;

    public static void rotationCheck(ClientPlayerEntity player) {
        // TODO if i rotate manually and then on the macro it will not work cuz the variable stays true
        if (Puslar.farmingMacro && !player.getHorizontalFacing().equals(Settings.direction) && !rotated) {
            rotated = true;
            player.sendMessage(ChatPrefix.WARNING.getPrefix().append("ROTATED"), false);
            if (Settings.debug) player.sendMessage(ChatPrefix.DEBUG.getPrefix().append("Delay on"), false);
            BackgroundTools.requestDelay(Settings.delayTicks);
        }
    }

    public static void brakeManager(ClientPlayerEntity player, World world) {
        boolean onBrakeBlock = isBrakeBlock(player, world);

        if (onBrakeBlock && !brakePending && !isOnBrake) {
            brakeDurationTicks = 140 + (int) (Math.random() * (1200 - 140));
            brakePending = true;
            player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Brake for " + brakeDurationTicks + " ticks"), false);
        }

        if (brakePending && !onBrakeBlock) {
            brakePending = false;
            isOnBrake = true;
            endBrakeTick = BackgroundTools.currentTick + brakeDurationTicks;
            Movement.stop();
            player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Brake started"), false);
        }

        if (isOnBrake && BackgroundTools.currentTick >= endBrakeTick) {
            isOnBrake = false;
            player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Brake ended"), false);
        }
    }

    private static boolean isBrakeBlock(ClientPlayerEntity player, World world) {
        BlockPos blockPos = player.getBlockPos().down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.getBlock().equals(Blocks.COARSE_DIRT);
    }
}
