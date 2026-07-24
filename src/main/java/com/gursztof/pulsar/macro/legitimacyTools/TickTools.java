package com.gursztof.pulsar.macro.legitimacyTools;

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.chat.ChatPrefix;
import com.gursztof.pulsar.chat.ChatSender;
import com.gursztof.pulsar.macro.Hitting;
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
            ChatSender.send("ROTATED", ChatPrefix.WARNING);
            ChatSender.send("Delay on", ChatPrefix.DEBUG);
            BackgroundTools.requestDelay(Settings.delayTicks);
        }
    }

    public static void brakeManager(ClientPlayerEntity player, World world) {
        boolean onBrakeBlock = isBrakeBlock(player, world);
        // Math random is for chances of NOT going for brake
        double breakRoll = Math.random() * 100;

        if (onBrakeBlock && !brakePending && !isOnBrake && breakRoll < Settings.brakeChance) {
            ChatSender.send(breakRoll + " is lower than " + Settings.brakeChance, ChatPrefix.DEBUG);

            brakeDurationTicks = 140 + (int) (Math.random() * (1200 - 140));
            brakePending = true;

            ChatSender.send("Brake for " + brakeDurationTicks + " ticks", ChatPrefix.PULSAR);
        }

        if (brakePending && !onBrakeBlock) {
            brakePending = false;
            isOnBrake = true;
            endBrakeTick = BackgroundTools.currentTick + brakeDurationTicks;

            Movement.stop();
            Hitting.stop();

            ChatSender.send("Brake started", ChatPrefix.PULSAR);
        }

        if (isOnBrake && BackgroundTools.currentTick >= endBrakeTick) {
            isOnBrake = false;
            ChatSender.send("Brake ended", ChatPrefix.PULSAR);
        }
    }

    private static boolean isBrakeBlock(ClientPlayerEntity player, World world) {
        BlockPos blockPos = player.getBlockPos().down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.getBlock().equals(Blocks.COARSE_DIRT);
    }
}
