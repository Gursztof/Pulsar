package com.gursztof.pulsar.macro.navigation;

import com.gursztof.pulsar.Direction.PathDirection;
import com.gursztof.pulsar.settings.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FindPath {
    public static boolean canBeRight = true;
    public static boolean canBeLeft = true;
    public static PathDirection alignTo = PathDirection.RIGHT;

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
            BlockState forwardLeftBlockState = world.getBlockState(forwardLeftBlockPos);
            Block forwardLeftBlock = forwardLeftBlockState.getBlock();

            BlockPos forwardRightBlockPos = blockForwardPos.offset(Settings.direction.rotateYClockwise(), count);
            BlockState forwardRightBlockState = world.getBlockState(forwardRightBlockPos);
            Block forwardRightBlock = forwardRightBlockState.getBlock();



            if (!forwardRightBlock.equals(Blocks.FARMLAND) || !forwardLeftBlock.equals(Blocks.FARMLAND)) {
                if (!forwardRightBlock.equals(Blocks.FARMLAND)) {
                    BlockState beforeRightBlock = world.getBlockState(forwardRightBlockPos.offset(Settings.direction.getOpposite(), 1));
                    if (beforeRightBlock.isAir() || beforeRightBlock.getBlock().equals(Blocks.WATER)) {
                        canBeLeft = false;
                        alignTo = PathDirection.RIGHT;
                        return PathDirection.RIGHT;
                    } else {
                        canBeRight = false;
                        alignTo = PathDirection.LEFT;
                        return PathDirection.LEFT;
                    }
                }

                if (!forwardLeftBlock.equals(Blocks.FARMLAND)) {
                    BlockState beforeLeftBlock = world.getBlockState(forwardLeftBlockPos.offset(Settings.direction.getOpposite(), 1));
                    if (beforeLeftBlock.isAir() || beforeLeftBlock.getBlock().equals(Blocks.WATER)) {
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
}
