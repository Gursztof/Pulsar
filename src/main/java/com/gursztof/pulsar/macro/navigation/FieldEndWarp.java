package com.gursztof.pulsar.navigation;

import com.gursztof.pulsar.macro.FarmingMacro;
import com.gursztof.pulsar.macro.TickClock;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FieldEndWarp {
    private static int warpTick;
    private static boolean warpRequest = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            if (isEndBlock(client.player, client.world) && !warpRequest) {
                int waitFor = (int) (Math.random() * 10);

                warpTick = TickClock.currentTick + waitFor;
                warpRequest = true;
            }

            if (warpRequest && TickClock.currentTick <= warpTick) {
                warpRequest = false;
                client.player.networkHandler.sendChatCommand("warp garden");
                FindPath.canBeLeft = true;
                FindPath.canBeRight = true;
            }
        });
    }

    private static boolean isEndBlock(ClientPlayerEntity player, World world) {
        BlockPos blockPos = player.getBlockPos().down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.getBlock().equals(Blocks.NOTE_BLOCK);
    }
}
