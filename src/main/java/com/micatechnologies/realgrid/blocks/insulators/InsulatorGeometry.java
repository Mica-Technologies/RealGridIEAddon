package com.micatechnologies.realgrid.blocks.insulators;

import com.micatechnologies.realgrid.util.BoundsUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

/**
 * Shape + wire-attachment data for an insulator variant. Used by
 * {@link TileEntityInsulatorBase} so every leaf TE is just a
 * constructor-param wrapper instead of a bespoke subclass.
 *
 * <p>Top-mount presets use a fixed bounding box and offset (no rotation).
 * Rotatable presets store NORTH-facing bounds and offset, then rotate
 * dynamically via {@link BoundsUtil}.
 *
 * <p>Five presets cover all current insulators:
 *   VISE_TOP   - Hendrix compact top-mount
 *   F_NECK     - Locke and MacLean PTI tall top-mount
 *   POST_TOP   - MacLean PI full top-mount
 *   SIDE_MOUNT - every direction-dependent side-mount variant
 *   DEAD_END   - dead-end insulators (elevated, direction-dependent)
 */
public final class InsulatorGeometry
{
    private final boolean usesRotation;
    private final Vec3d northOffset;
    private final float[] northBounds;

    private InsulatorGeometry(boolean usesRotation, Vec3d northOffset, float[] northBounds)
    {
        this.usesRotation = usesRotation;
        this.northOffset = northOffset;
        this.northBounds = northBounds;
    }

    /** Creates a top-mount geometry with fixed (non-rotating) bounds and offset. */
    public static InsulatorGeometry top(Vec3d offset, float[] bounds)
    {
        return new InsulatorGeometry(false, offset, bounds);
    }

    /** Creates a direction-dependent geometry that rotates bounds/offset by facing. */
    public static InsulatorGeometry rotatable(Vec3d northOffset, float[] northBounds)
    {
        return new InsulatorGeometry(true, northOffset, northBounds);
    }

    public Vec3d connectionOffset(EnumFacing facing)
    {
        return usesRotation ? BoundsUtil.rotateOffset(northOffset, facing) : northOffset;
    }

    public float[] blockBounds(EnumFacing facing)
    {
        return usesRotation ? BoundsUtil.rotateBounds(northBounds, facing) : northBounds;
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

    /**
     * Every direction-dependent side-mount insulator.
     * NORTH-facing bounds: narrow in X, extends along Z toward the facing direction.
     */
    public static final InsulatorGeometry SIDE_MOUNT = rotatable(
        new Vec3d(0.5, 0.5625, 0.125),
        new float[]{0.3125f, 0.0f, 0.0625f, 0.6875f, 0.6875f, 0.9375f}
    );

    /**
     * Dead-end insulators: elevated models that extend along the facing direction.
     * Bounds are raised on Y to match the actual model position.
     */
    public static final InsulatorGeometry DEAD_END = rotatable(
        new Vec3d(0.5, 0.6875, 0.125),
        new float[]{0.25f, 0.375f, 0.0f, 0.75f, 0.8125f, 1.0f}
    );
}
