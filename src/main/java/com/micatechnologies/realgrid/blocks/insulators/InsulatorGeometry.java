package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

/**
 * Shape + wire-attachment data for an insulator variant. Used by
 * {@link TileEntityInsulatorBase} so every leaf TE is just a
 * constructor-param wrapper instead of a bespoke subclass.
 *
 * Four presets cover all current insulators:
 *   VISE_TOP   - Hendrix compact top-mount
 *   F_NECK     - Locke and MacLean PTI tall top-mount
 *   POST_TOP   - MacLean PI full top-mount
 *   SIDE_MOUNT - every direction-dependent side-mount variant
 */
public final class InsulatorGeometry
{
    private final boolean sideMount;
    private final Vec3d topOffset;
    private final float[] topBounds;
    private final double sideY;
    private final float[] sideBoundsNS;
    private final float[] sideBoundsEW;

    private InsulatorGeometry(Vec3d topOffset, float[] topBounds)
    {
        this.sideMount = false;
        this.topOffset = topOffset;
        this.topBounds = topBounds;
        this.sideY = 0;
        this.sideBoundsNS = null;
        this.sideBoundsEW = null;
    }

    private InsulatorGeometry(double sideY, float[] nsBounds, float[] ewBounds)
    {
        this.sideMount = true;
        this.topOffset = null;
        this.topBounds = null;
        this.sideY = sideY;
        this.sideBoundsNS = nsBounds;
        this.sideBoundsEW = ewBounds;
    }

    public static InsulatorGeometry top(Vec3d offset, float[] bounds)
    {
        return new InsulatorGeometry(offset, bounds);
    }

    public static InsulatorGeometry side(double y, float[] nsBounds, float[] ewBounds)
    {
        return new InsulatorGeometry(y, nsBounds, ewBounds);
    }

    public Vec3d connectionOffset(EnumFacing facing)
    {
        if (!sideMount) return topOffset;
        double dx = 0.5, dz = 0.5;
        if (facing == EnumFacing.NORTH)      dz = 0.125;
        else if (facing == EnumFacing.SOUTH) dz = 0.875;
        else if (facing == EnumFacing.WEST)  dx = 0.125;
        else if (facing == EnumFacing.EAST)  dx = 0.875;
        return new Vec3d(dx, sideY, dz);
    }

    public float[] blockBounds(EnumFacing facing)
    {
        if (!sideMount) return topBounds;
        return (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
            ? sideBoundsNS
            : sideBoundsEW;
    }

    // === Presets ===

    /** Hendrix vise-top: compact post, wire rests at the top of the vise groove. */
    public static final InsulatorGeometry VISE_TOP = top(
        new Vec3d(0.5, 0.875, 0.5),
        new float[]{0.3125f, 0.0f, 0.3125f, 0.6875f, 0.875f, 0.6875f}
    );

    /** Locke / MacLean PTI tall post with narrow f-neck at the top. */
    public static final InsulatorGeometry F_NECK = top(
        new Vec3d(0.5, 0.8125, 0.5),
        new float[]{0.3125f, 0.0f, 0.3125f, 0.6875f, 0.9375f, 0.6875f}
    );

    /** MacLean PI full-width post-top insulator. */
    public static final InsulatorGeometry POST_TOP = top(
        new Vec3d(0.5, 0.9375, 0.5),
        new float[]{0.25f, 0.0f, 0.25f, 0.75f, 0.9375f, 0.75f}
    );

    /** Every direction-dependent side-mount insulator. */
    public static final InsulatorGeometry SIDE_MOUNT = side(
        0.5625,
        new float[]{0.3125f, 0.0f, 0.0625f, 0.6875f, 0.6875f, 0.9375f},
        new float[]{0.0625f, 0.0f, 0.3125f, 0.9375f, 0.6875f, 0.6875f}
    );
}
