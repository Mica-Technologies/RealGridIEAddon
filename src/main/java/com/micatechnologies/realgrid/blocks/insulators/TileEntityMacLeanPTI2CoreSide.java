package com.micatechnologies.realgrid.blocks.insulators;

import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

/**
 * Porcelain Dead End Insulator — color variants: white (0) and red (1).
 * For terminating wire runs. Acts as a wire relay.
 *
 * Color NBT persistence inherited from {@link TileEntityColoredInsulatorBase}.
 */
public class TileEntityMacLeanPTI2CoreSide extends TileEntityColoredInsulatorBase {

    @Override
    public Vec3d getConnectionOffset(Connection con) {
        double dx = 0.5;
        double dy = 0.5625;
        double dz = 0.5;

        if (facing == EnumFacing.NORTH) dz = 0.125;
        else if (facing == EnumFacing.SOUTH) dz = 0.875;
        else if (facing == EnumFacing.WEST) dx = 0.125;
        else if (facing == EnumFacing.EAST) dx = 0.875;

        return new Vec3d(dx, dy, dz);
    }

    @Override
    public float[] getBlockBounds() {
        if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
            return new float[]{ 0.3125f, 0.0f, 0.0625f, 0.6875f, 0.6875f, 0.9375f };
        else
            return new float[]{ 0.0625f, 0.0f, 0.3125f, 0.9375f, 0.6875f, 0.6875f };
    }
}
