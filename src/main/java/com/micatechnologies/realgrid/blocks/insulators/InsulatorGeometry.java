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
 * Rotatable presets store the bounds and offset for the orientation their model is
 * drawn in, and state which that is, then rotate dynamically via {@link BoundsUtil}.
 *
 * <p>Five presets cover all current insulators:
 *   VISE_TOP   - Hendrix compact top-mount
 *   F_NECK     - Locke and MacLean PTI tall top-mount
 *   POST_TOP   - MacLean PI full top-mount
 *   SIDE_MOUNT - every direction-dependent side-mount variant (drawn facing north)
 *   DEAD_END   - dead-end insulators (elevated, direction-dependent, drawn facing south)
 */
public final class InsulatorGeometry
{
    private final boolean usesRotation;
    private final EnumFacing authoredFacing;
    private final Vec3d baseOffset;
    private final float[] baseBounds;

    private InsulatorGeometry(boolean usesRotation, EnumFacing authoredFacing, Vec3d baseOffset, float[] baseBounds)
    {
        this.usesRotation = usesRotation;
        this.authoredFacing = authoredFacing;
        this.baseOffset = baseOffset;
        this.baseBounds = baseBounds;
    }

    /** Creates a top-mount geometry with fixed (non-rotating) bounds and offset. */
    public static InsulatorGeometry top(Vec3d offset, float[] bounds)
    {
        return new InsulatorGeometry(false, null, offset, bounds);
    }

    /**
     * Creates a direction-dependent geometry that rotates bounds/offset by facing.
     *
     * @param authoredFacing the facing the supplied bounds and offset describe, which must match the
     *                       orientation the block's own model is drawn in -- the facing its blockstate
     *                       leaves unrotated. The side-mount blockstates are drawn facing north; the
     *                       dead-end ones are drawn facing south.
     */
    public static InsulatorGeometry rotatable(EnumFacing authoredFacing, Vec3d baseOffset, float[] baseBounds)
    {
        return new InsulatorGeometry(true, authoredFacing, baseOffset, baseBounds);
    }

    /**
     * {@link BoundsUtil} rotates data describing the north-facing orientation. Data drawn facing south
     * is half a turn out from that, so it has to be rotated to the opposite facing to land in the same
     * place as the model.
     */
    private EnumFacing rotationFor(EnumFacing facing)
    {
        return authoredFacing == EnumFacing.SOUTH ? facing.getOpposite() : facing;
    }

    public Vec3d connectionOffset(EnumFacing facing)
    {
        return usesRotation ? BoundsUtil.rotateOffset(baseOffset, rotationFor(facing)) : baseOffset;
    }

    public float[] blockBounds(EnumFacing facing)
    {
        return usesRotation ? BoundsUtil.rotateBounds(baseBounds, rotationFor(facing)) : baseBounds;
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
     * Every direction-dependent side-mount insulator. Those blockstates leave north unrotated, so the
     * bounds and offset below describe the north-facing orientation: narrow in X, extending along Z
     * toward the facing direction.
     */
    public static final InsulatorGeometry SIDE_MOUNT = rotatable(
        EnumFacing.NORTH,
        new Vec3d(0.5, 0.5625, 0.125),
        new float[]{0.3125f, 0.0f, 0.0625f, 0.6875f, 0.6875f, 0.9375f}
    );

    /**
     * Dead-end insulators: elevated models that extend along the facing direction. Bounds are raised on
     * Y to match the actual model position.
     *
     * <p>Unlike the side-mounts, these blockstates leave <em>south</em> unrotated, so the figures below
     * describe the south-facing orientation. Rotating them as though they were north-facing put the wire
     * and the bounding box on the opposite side of the block from the insulator's eye in every one of
     * the four facings.
     */
    public static final InsulatorGeometry DEAD_END = rotatable(
        EnumFacing.SOUTH,
        new Vec3d(0.5, 0.6875, 0.125),
        new float[]{0.25f, 0.375f, 0.0f, 0.75f, 0.8125f, 1.0f}
    );
}
