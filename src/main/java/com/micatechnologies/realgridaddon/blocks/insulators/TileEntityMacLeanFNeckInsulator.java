package com.micatechnologies.realgridaddon.blocks.insulators;

import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

/**
 * MacLean F-Neck Distribution Line Post Insulator
 * Acts as a wire relay for HV/MV/LV wires.
 * Sits on top of posts/poles.
 */
public class TileEntityMacLeanFNeckInsulator extends TileEntityInsulatorBase
{
    @Override
    public Vec3d getConnectionOffset(Connection con)
    {
        // F-Neck insulator - wire connects at the neck groove
        double dx = 0.5;
        double dy = 0.8125;
        double dz = 0.5;

        // Offset slightly based on facing for a more natural wire attachment
        if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
        {
            dx = 0.5;
            dz = 0.5;
        }
        else
        {
            dx = 0.5;
            dz = 0.5;
        }

        return new Vec3d(dx, dy, dz);
    }

    @Override
    public float[] getBlockBounds()
    {
        // F-Neck shape: narrow post with wider top
        return new float[]{0.3125f, 0.0f, 0.3125f, 0.6875f, 0.9375f, 0.6875f};
    }
}
