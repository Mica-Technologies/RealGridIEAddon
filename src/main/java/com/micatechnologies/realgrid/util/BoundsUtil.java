package com.micatechnologies.realgrid.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

/**
 * Utility for rotating bounding boxes and connection offsets by horizontal
 * {@link EnumFacing}. All inputs are assumed to describe the NORTH-facing
 * orientation; the utility rotates them clockwise around the block centre.
 *
 * <p>Adapted from CSM's {@code RotationUtils} for the {@code float[]} format
 * used by IE's {@code IBlockBounds} interface.
 */
public final class BoundsUtil
{
    private BoundsUtil() {}

    /**
     * Rotates a NORTH-facing bounding box ({@code [minX, minY, minZ, maxX, maxY, maxZ]})
     * to align with the given horizontal facing.
     */
    public static float[] rotateBounds(float[] northBounds, EnumFacing facing)
    {
        float minX = northBounds[0], minY = northBounds[1], minZ = northBounds[2];
        float maxX = northBounds[3], maxY = northBounds[4], maxZ = northBounds[5];

        switch (facing)
        {
            case SOUTH:
                return new float[]{ 1f - maxX, minY, 1f - maxZ, 1f - minX, maxY, 1f - minZ };
            case EAST:
                return new float[]{ 1f - maxZ, minY, minX, 1f - minZ, maxY, maxX };
            case WEST:
                return new float[]{ minZ, minY, 1f - maxX, maxZ, maxY, 1f - minX };
            default: // NORTH or unexpected
                return northBounds;
        }
    }

    /**
     * Rotates a NORTH-facing connection offset to align with the given
     * horizontal facing.
     */
    public static Vec3d rotateOffset(Vec3d northOffset, EnumFacing facing)
    {
        double x = northOffset.x, y = northOffset.y, z = northOffset.z;
        switch (facing)
        {
            case SOUTH:
                return new Vec3d(1.0 - x, y, 1.0 - z);
            case EAST:
                return new Vec3d(1.0 - z, y, x);
            case WEST:
                return new Vec3d(z, y, 1.0 - x);
            default: // NORTH or unexpected
                return northOffset;
        }
    }
}
