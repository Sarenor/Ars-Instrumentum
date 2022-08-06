package de.sarenor.arsinstrumentum.utils;

import net.minecraft.core.BlockPos;

public class BlockPosUtils {
    public static String toString(BlockPos blockPos) {
        return "{X: " + blockPos.getX() + ", Y: " + blockPos.getY() + ", Z: " + blockPos.getZ() + "}";
    }

    public static Iterable<BlockPos> getNeighbours(BlockPos center) {
        return BlockPos.betweenClosed(center.below().east().north(), center.above().west().south());
    }

    public static boolean isNeighbour(BlockPos center, BlockPos possibleNeighbour) {
        for (BlockPos p : getNeighbours(center)) {
            if (p.equals(possibleNeighbour)) {
                return true;
            }
        }
        return false;
    }
}
