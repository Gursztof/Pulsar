//package com.gursztof.pulsar.functionality;
//
//import com.gursztof.pulsar.Puslar;
//import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.client.network.ClientPlayerEntity;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Text;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//public class PosChecker {
//    public static void init(String test) {
//        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//            if (!Puslar.posChecker) {
//                return;
//            }
//
//            ClientPlayerEntity player = client.player;
//            assert player != null;
//            double x = player.getX();
//            double y = player.getY();
//            double z = player.getZ();
//            player.sendMessage(Text.literal("x: " + x + ", y: " + y + ", z: " + z), false);
//        });
//    }
//
//    public static void init() {
//        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//            // Check if command is on
//            if (!Puslar.posChecker) {
//                return;
//            }
//            World world = client.world;
//            ClientPlayerEntity player = client.player;
//            assert player != null;
//            assert world != null;
//
//            BlockPos pos = player.getBlockPos();
//            BlockState stateFront = null;
//            BlockState stateLeft = null;
//            BlockState stateRight = null;
//
//            boolean isAirOnLeft = false;
//            boolean isAirOnRight = false;
//            boolean isFarmlandOnFront = false;
//            switch (direction) {
//                case NORTH -> {
//                    stateFront = world.getBlockState(pos.north());
//                    stateLeft = world.getBlockState(pos.west(1));
//                    stateRight = world.getBlockState(pos.east(1));
//                }
//                case EAST -> {
//                    stateFront = world.getBlockState(pos.east());
//                    stateLeft = world.getBlockState(pos.north(1));
//                    stateRight = world.getBlockState(pos.south(1));
//                }
//                case SOUTH -> {
//                    stateFront = world.getBlockState(pos.south());
//                    stateLeft = world.getBlockState(pos.east(1));
//                    stateRight = world.getBlockState(pos.west(1));
//                }
//                case WEST -> {
//                    stateFront = world.getBlockState(pos.west());
//                    stateLeft = world.getBlockState(pos.south(1));
//                    stateRight = world.getBlockState(pos.north(1));
//                }
//            }
//
//            Block blockFront = stateFront.getBlock();
//            Block blockLeft = stateLeft.getBlock();
//            Block blockRight = stateRight.getBlock();
//            if (blockLeft.equals(Blocks.AIR)) isAirOnLeft = true;
//            if (blockRight.equals(Blocks.AIR)) isAirOnRight = true;
//            if (blockFront.equals(Blocks.FARMLAND)) isFarmlandOnFront = true;
//            if (blockFront.equals(Blocks.FARMLAND)) isFarmlandOnFront = true;
//
//            if (player.isOnGround()) {
//                if (!isFarmlandOnFront) {
//                    player.sendMessage(Text.literal("Straight"), false);
//
//                } else {
//                    if (lastTurnWasLeft) {
//                        player.sendMessage(Text.literal("Right"), false);
//                        if (!isAirOnRight) {
//                            lastTurnWasLeft = false;
//                        }
//                    } else {
//                        player.sendMessage(Text.literal("Left"), false);
//                        if (!isAirOnLeft) {
//                            lastTurnWasLeft = true;
//                        }
//                    }
//                }
//            }
//        });
//    }

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.macro.Movement;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//public static void fakePause() {
//    ClientTickEvents.START_CLIENT_TICK.register(client -> {
//        ClientPlayerEntity player = client.player;
//        World world = client.world;
//
//        if (player == null || world == null || pause) return;
//
//        if (isPauseBlock(player, world)) {
//            // Around 25% chances of happening
//            if (Math.random() * 100 - 1 < 25) {
//                player.sendMessage(Text.literal("Fake Brake"), false);
//                // Brake from 7 sec to 60 sec
//                pauseFor((int) (Math.random() * 1200 - 140));
//                player.sendMessage(Text.literal("Brake time (ticks): " + (endPauseTick - currentTick)), false);
//            }
//        }
//    });
//}
//private static boolean isPauseBlock(ClientPlayerEntity player, World world) {
//    BlockPos blockPos = player.getBlockPos().down();
//    BlockState blockState = world.getBlockState(blockPos);
//    return blockState.getBlock().equals(Blocks.COARSE_DIRT);
//}

// Cant move after hitting brake
//public static void pauseFor(int ticks) {
//    if (!pause) {
//        endPauseTick = currentTick + ticks;
//    }
//
//    ClientTickEvents.END_CLIENT_TICK.register(client -> {
//        ClientPlayerEntity player = client.player;
//        World world = client.world;
//
//        if (player == null || world == null) return;
//
//        pause = currentTick < endPauseTick;
//
//
//        if (!isPauseBlock(player, world) && pause) {
//            Movement.resetMove();
//            Puslar.farmingMacro = false;
//        }
//    });
//}
//
//public static void stop() {
//    Puslar.farmingMacro = false;
//    pause = false;
//    Movement.resetMove();
//}

// Do last movement for (ticks)
//    static int endWaitTick;
//    static boolean wait;
//    public static void waitForBeforStop(int ticks) {
//        if (!wait) {
//            endWaitTick = currentTick + ticks;
//            wait = true;
//            return;
//        }
//
//        if (currentTick == endWaitTick) {
//            stop();
//        }
//    }
//}
