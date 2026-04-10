package com.micatechnologies.realgrid.blocks.insulators;

import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import net.minecraft.util.math.Vec3d;

/**
 * Hendrix Vise Top Insulator
 * Modern distribution line insulator.
 * Acts as a wire relay for HV/MV/LV.
 */
public class TileEntityHendrixVTI2Core extends TileEntityInsulatorBase
{
    @Override
    public Vec3d getConnectionOffset(Connection con)
    {
        // Vise top - wire connects at the top of the vise groove
        return new Vec3d(0.5, 0.875, 0.5);
    }

    @Override
    public float[] getBlockBounds()
    {
        // Vise top shape: compact insulator
        return new float[]{0.3125f, 0.0f, 0.3125f, 0.6875f, 0.875f, 0.6875f};
    }
}
