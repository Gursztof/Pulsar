package com.gursztof.pulsar.macro;

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.chat.ChatPrefix;
import com.gursztof.pulsar.chat.ChatSender;
import com.gursztof.pulsar.macro.legitimacyTools.TickTools;
import com.gursztof.pulsar.macroDirection.PathDirection;
import com.gursztof.pulsar.settings.Settings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FarmingMacro {
    public static boolean canBeRight = true;
    public static boolean canBeLeft = true;
    public static PathDirection alignTo = PathDirection.RIGHT;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            World world = client.world;

            if (player == null || world == null || !Puslar.farmingMacro) {
                return;
            }

            // Those features work only if macro is on
            TickTools.brakeManager(player, world);
            TickTools.rotationCheck(player);

            // TODO add delay to end block
            if (isEndBlock(player, world)) {
                ChatSender.send("End block", ChatPrefix.DEBUG);
                if (client.getServer() == null) return;
                if (!client.isInSingleplayer()) {
                    ChatSender.send("Server was detected", ChatPrefix.DEBUG);
                    player.networkHandler.sendChatCommand("warp garden");
                }
            }

            PathDirection pathDirection = findPath(player, world);

            // Debug direction info
            ChatSender.send("Direction: " + pathDirection, ChatPrefix.DEBUG);

            if (!TickTools.isOnBrake && player.isOnGround()) {
                Hitting.start();

                if (pathDirection.equals(PathDirection.FORWARD)) {
                    if (!isAlignedForGo(player)) {
                        Movement.go(alignTo);
                    } else {
                        Movement.go(PathDirection.FORWARD);
                    }
                } else if (pathDirection.equals(PathDirection.LEFT) || pathDirection.equals(PathDirection.RIGHT)) {
                    if (!isAlignedForTurn(player)) {
                        Movement.go(PathDirection.FORWARD);
                    } else {
                        if (pathDirection.equals(PathDirection.RIGHT)) {
                            Movement.go(PathDirection.RIGHT);
                        } else {
                            Movement.go(PathDirection.LEFT);
                        }
                    }
                }
            }
        });
    }

    private static boolean isAlignedForTurn(ClientPlayerEntity player) {
        double randomLegitError = Math.random() * 0.153;
        double frac;
        switch (Settings.direction) {
            case NORTH, SOUTH -> {
                frac = player.getZ() - player.getBlockZ();
                ChatSender.send(String.valueOf(frac), ChatPrefix.DEBUG);
            }
            case EAST, WEST -> {
                frac = player.getX() - player.getBlockX();
                ChatSender.send(String.valueOf(frac), ChatPrefix.DEBUG);
            }
            default -> { return false; }
        }

        return switch (Settings.direction) {
            case NORTH, WEST -> frac < 0.5 + randomLegitError;
            case SOUTH, EAST -> frac >= 0.5 + randomLegitError;
            default -> false;
        };
    }

    private static boolean isAlignedForGo(ClientPlayerEntity player) {
        double randomLegitError = Math.random() * 0.153;
        double frac = 0;
        switch (Settings.direction) {
            case NORTH, SOUTH -> {
                frac = player.getX() - player.getBlockX();
                ChatSender.send(String.valueOf(frac), ChatPrefix.DEBUG);
            }
            case EAST, WEST -> {
                frac = player.getZ() - player.getBlockZ();
                ChatSender.send(String.valueOf(frac), ChatPrefix.DEBUG);
            }
        }

        return switch (Settings.direction) {
            case NORTH, EAST -> {
                if (alignTo.equals(PathDirection.LEFT)) {
                    // Wall is on right at end
                    yield frac <= 0.5 + randomLegitError;
                }
                // Wall is on right at end
                yield frac >= 0.5 - randomLegitError;
            }
            case SOUTH, WEST -> {
                if (alignTo.equals(PathDirection.LEFT)) {
                    // Wall is on left at end
                    yield frac >= 0.5 - randomLegitError;
                }
                // Wall is on right at end
                yield frac <= 0.5 + randomLegitError;
            }
            default -> false;
        };
    }

    public static PathDirection findPath(ClientPlayerEntity player, World world) {
        int count = 0;

        BlockPos playerPos = player.getBlockPos();
        BlockPos blockForwardPos = playerPos.offset(Settings.direction, 1);
        BlockState blockStateForward = world.getBlockState(blockForwardPos);
        Block blockForward = blockStateForward.getBlock();

        if (blockStateForward.isAir() || blockForward.equals(Blocks.WATER)) {
            canBeRight = true;
            canBeLeft = true;
            return PathDirection.FORWARD;
        }

        if (!canBeRight) {
            return PathDirection.LEFT;
        } else if (!canBeLeft) {
            return PathDirection.RIGHT;
        }

        while (count < Settings.maxDistance) {
            count++;

            BlockPos forwardLeftBlockPos = blockForwardPos.offset(Settings.direction.rotateYCounterclockwise(), count);
            BlockState forwardLeftBlock = world.getBlockState(forwardLeftBlockPos);

            BlockPos forwardRightBlockPos = blockForwardPos.offset(Settings.direction.rotateYClockwise(), count);
            BlockState forwardRightBlock = world.getBlockState(forwardRightBlockPos);

            if (forwardRightBlock.isAir() || forwardLeftBlock.isAir()) {
                if (forwardRightBlock.isAir()) {
                    BlockState beforeRightBlock = world.getBlockState(forwardRightBlockPos.offset(Settings.direction.getOpposite(), 1));
                    if (beforeRightBlock.isAir()) {
                        canBeLeft = false;
                        alignTo = PathDirection.RIGHT;
                        return PathDirection.RIGHT;
                    } else {
                        canBeRight = false;
                        alignTo = PathDirection.LEFT;
                        return PathDirection.LEFT;
                    }
                }

                if (forwardLeftBlock.isAir()) {
                    BlockState beforeLeftBlock = world.getBlockState(forwardLeftBlockPos.offset(Settings.direction.getOpposite(), 1));
                    if (beforeLeftBlock.isAir()) {
                        canBeRight = false;
                        alignTo = PathDirection.LEFT;
                        return PathDirection.LEFT;
                    } else {
                        canBeLeft = false;
                        alignTo = PathDirection.RIGHT;
                        return PathDirection.RIGHT;
                    }
                }
            }
        }

        return PathDirection.NOWAY;
    }

    private static boolean isEndBlock(ClientPlayerEntity player, World world) {
        BlockPos blockPos = player.getBlockPos().down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.getBlock().equals(Blocks.NOTE_BLOCK);
    }
}
