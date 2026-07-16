package com.gursztof.pulsar.macro;

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.chat.ChatPrefix;
import com.gursztof.pulsar.settings.Settings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FarmingMacro {
    public static boolean lastTurnWasLeft;
    private static boolean isAirOnLeft;
    private static boolean isAirOnRight;
    private static boolean isFarmlandOnFront;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            World world = client.world;
            if (player == null || world == null || !Puslar.farmingMacro) {
                return;
            }

            if (isEndBlock(player, world) && Settings.debug) {
                player.sendMessage(ChatPrefix.INFO.getPrefix().append("Warp garden"), false);
            }

            AntiBanFeatures.rotationCheck();
            checkBlocksAround(player, world);

            if (player.isOnGround() && !AntiBanFeatures.isOnBrake) {
                // Check if we can go straight
                if (!isFarmlandOnFront && isAlignedForGo(player, lastTurnWasLeft)) {
                    Movement.goForward();
                } else if (isFarmlandOnFront && !isAlignedForTurn(player)) {
                    Movement.goForward();
                    lastTurnWasLeft = isLastDirectionLeft();
                } // If not that means we go sideways
                  else {
                    if (lastTurnWasLeft) {
                        Movement.goRight();
                    } else {
                        Movement.goLeft();
                    }
                }
            }
        });
    }

    private static boolean isLastDirectionLeft() {
        switch (Settings.direction) {
            case NORTH, WEST -> {
                return lastTurnWasLeft = isAirOnRight;
            }
            case SOUTH, EAST -> {
                return lastTurnWasLeft = !isAirOnLeft;
            }
        }
        return  false;
    }

    private static boolean isEndBlock(ClientPlayerEntity player, World world) {
        BlockPos blockPos = player.getBlockPos().down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.getBlock().equals(Blocks.NOTE_BLOCK);
    }
    private static void checkBlocksAround(ClientPlayerEntity player, World world) {
        BlockPos pos = player.getBlockPos();

        BlockPos front = pos.offset(Settings.direction);
        BlockPos left = pos.offset(Settings.direction.rotateYCounterclockwise());
        BlockPos right = pos.offset(Settings.direction.rotateYClockwise());

        BlockState stateFront = world.getBlockState(front);
        BlockState stateLeft = world.getBlockState(left);
        BlockState stateRight = world.getBlockState(right);

        isAirOnLeft = stateLeft.isAir();
        isAirOnRight = stateRight.isAir();
        isFarmlandOnFront = stateFront.getBlock().equals(Blocks.FARMLAND);
    }

    private static boolean isAlignedForTurn(ClientPlayerEntity player) {
        double randomLegitError = Math.random() * 0.153;
        double frac;
        switch (Settings.direction) {
            case NORTH, SOUTH -> {
                frac = player.getZ() - player.getBlockZ();
                if (Settings.debug) {
                    player.sendMessage(ChatPrefix.INFO.getPrefix().append(String.valueOf(frac)), false);
                }
            }
            case EAST, WEST -> {
                frac = player.getX() - player.getBlockX();
                if (Settings.debug) {
                    player.sendMessage(ChatPrefix.INFO.getPrefix().append(String.valueOf(frac)), false);
                }
            }
            default -> { return false; }
        }

        return switch (Settings.direction) {
            case NORTH, WEST -> frac < 0.5 + randomLegitError;
            case SOUTH, EAST -> frac >= 0.5 + randomLegitError;
            default -> false;
        };
    }
    private static boolean isAlignedForGo(ClientPlayerEntity player, boolean lastTurnWasLeft) {
        double randomLegitError = Math.random() * 0.153;
        double frac = 0;
        switch (Settings.direction) {
            case NORTH, SOUTH -> {
                frac = player.getX() - player.getBlockX();
                if (Settings.debug) {
                    player.sendMessage(ChatPrefix.INFO.getPrefix().append(String.valueOf(frac)), false);
                }
            }
            case EAST, WEST -> {
                frac = player.getZ() - player.getBlockZ();
                if (Settings.debug) {
                    player.sendMessage(ChatPrefix.INFO.getPrefix().append(String.valueOf(frac)), false);
                }
            }
        }

        return switch (Settings.direction) {
            case NORTH, EAST -> {
                if (lastTurnWasLeft) {
                    // Wall is on right at end
                    yield frac >= 0.5 + randomLegitError;
                }
                // Wall is on right at end
                yield frac <= 0.5 - randomLegitError;
            }
            case SOUTH, WEST -> {
                if (lastTurnWasLeft) {
                    // Wall is on left at end
                    yield frac <= 0.5 - randomLegitError;
                }
                // Wall is on right at end
                yield frac >= 0.5 + randomLegitError;
            }
            default -> false;
        };
    }
}
