package com.gursztof.pulsar.macro;

import com.gursztof.pulsar.Puslar;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FarmingMacro {
    public static Direction direction;
    public static boolean lastTurnWasLeft;
    private static boolean isAirOnLeft;
    private static boolean isAirOnRight;
    private static boolean isFarmlandOnFront;

    private static boolean isEndBlock(ClientPlayerEntity player, World world) {
        BlockPos blockPos = player.getBlockPos().down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.getBlock().equals(Blocks.NOTE_BLOCK);
    }

    private static boolean isAlignedForTurn(ClientPlayerEntity player) {
        double randomLegitError = Math.random() * 0.153;
        double frac;
        switch (direction) {
            case NORTH, SOUTH -> {
                frac = player.getZ() - player.getBlockZ();
                player.sendMessage(Text.literal(String.valueOf(frac)), false);
            }
            case EAST, WEST -> {
                frac = player.getX() - player.getBlockX();
                player.sendMessage(Text.literal(String.valueOf(frac)), false);
            }
            default -> { return false; }
        }

        return switch (direction) {
            case NORTH, WEST -> frac < 0.5 + randomLegitError;
            case SOUTH, EAST -> frac >= 0.5 + randomLegitError;
            default -> false;
        };
    }

    private static boolean isLastDirectionLeft() {
        switch (direction) {
            case NORTH, WEST -> {
                return lastTurnWasLeft = isAirOnRight;
            }
            case SOUTH, EAST -> {
                return lastTurnWasLeft = !isAirOnLeft;
            }
        }
        return  false;
    }

    private static boolean isAlignedForGo(ClientPlayerEntity player, boolean lastTurnWasLeft) {
        double randomLegitError = Math.random() * 0.153;
        double frac = 0;
        switch (direction) {
            case NORTH, SOUTH -> frac = player.getX() - player.getBlockX();
            case EAST, WEST -> frac = player.getZ() - player.getBlockZ();
        }

        return switch (direction) {
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

    private static void checkBlocksAround(ClientPlayerEntity player, World world) {
        BlockPos pos = player.getBlockPos();

        BlockPos front = pos.offset(direction);
        BlockPos left = pos.offset(direction.rotateYCounterclockwise());
        BlockPos right = pos.offset(direction.rotateYClockwise());

        BlockState stateFront = world.getBlockState(front);
        BlockState stateLeft = world.getBlockState(left);
        BlockState stateRight = world.getBlockState(right);

        isAirOnLeft = stateLeft.isAir();
        isAirOnRight = stateRight.isAir();
        isFarmlandOnFront = stateFront.getBlock().equals(Blocks.FARMLAND);
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            World world = client.world;
            if (player == null || world == null || !Puslar.farmingMacro) {
                return;
            }

            if (isEndBlock(player, world)) {
                player.networkHandler.sendChatCommand("garden");
            }

            AntiBanFeatures.rotationCheck();
            checkBlocksAround(player, world);

            if (player.isOnGround() && !AntiBanFeatures.isOnBrake) {
                // Check if we can go straight
                if (!isFarmlandOnFront && isAlignedForGo(player, lastTurnWasLeft)) {
                    player.sendMessage(Text.literal("GO!"), false);
                    Movement.goForward();
                } else if (isFarmlandOnFront && !isAlignedForTurn(player)) {
                    player.sendMessage(Text.literal("Go little bit!"), false);
                    Movement.goForward();
                    lastTurnWasLeft = isLastDirectionLeft();
                } // If not that means we go sideways
                  else {
                    if (lastTurnWasLeft) {
                        player.sendMessage(Text.literal("Right!"), false);
                        Movement.goRight();
                    } else {
                        player.sendMessage(Text.literal("Left!"), false);
                        Movement.goLeft();
                    }

                }
            }
        });
    }
}
