package com.micatechnologies.realgrid.blocks.insulators;

import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import net.minecraft.util.math.Vec3d;

/**
 * Porcelain Post Top Insulator — color variants: white (0) and black (1).
 * Acts as a wire relay.
 *
 * Color NBT persistence inherited from {@link TileEntityColoredInsulatorBase}.
 */
public class TileEntityMacLeanPINewSmall2 extends TileEntityColoredInsulatorBase {

    @Override
    public Vec3d getConnectionOffset(Connection con) {
        return new Vec3d(0.5, 0.9375, 0.5);
    }

    @Override
    public float[] getBlockBounds() {
        return new float[]{ 0.25f, 0.0f, 0.25f, 0.75f, 0.9375f, 0.75f };
    }
}
