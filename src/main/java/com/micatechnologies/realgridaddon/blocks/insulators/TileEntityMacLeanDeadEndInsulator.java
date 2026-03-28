package com.micatechnologies.realgridaddon.blocks.insulators;

import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

/**
 * MacLean Dead End Insulator
 * For terminating wire runs on poles.
 * Acts as a wire relay.
 */
public class TileEntityMacLeanDeadEndInsulator extends TileEntityInsulatorBase
{
    @Override
    public Vec3d getConnectionOffset(Connection con)
    {
        // Dead end insulator - wire connects at the end
        double dx = 0.5;
        double dy = 0.625;
        double dz = 0.5;

        // Offset along the facing direction for dead-end style connection
        if (facing == EnumFacing.NORTH)
            dz = 0.125;
        else if (facing == EnumFacing.SOUTH)
            dz = 0.875;
        else if (facing == EnumFacing.WEST)
            dx = 0.125;
        else if (facing == EnumFacing.EAST)
            dx = 0.875;

        return new Vec3d(dx, dy, dz);
    }

    @Override
    public float[] getBlockBounds()
    {
        // Dead end insulator: elongated in facing direction
        if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
            return new float[]{0.3125f, 0.0f, 0.0625f, 0.6875f, 0.75f, 0.9375f};
        else
            return new float[]{0.0625f, 0.0f, 0.3125f, 0.9375f, 0.75f, 0.6875f};
    }
}
