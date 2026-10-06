package com.micatechnologies.realgrid.blocks.crossarms;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

/**
 * Where a block that hangs from its back (a dead-end or side-mount insulator, a cutoff switch) is
 * fixed to a crossarm, seen from the block: against the arm's front face, its back face, or one of
 * its ends. Worked out from the block behind it, so nothing is stored.
 *
 * <p>Crossarm segments are flush against their pole at the top back of their blocks (6 px square,
 * 10-16 px deep), so a block hung off one has to move to meet it: {@link #offset} says how far, in
 * the block's own facing frame, which is also how scripts/gen_insulator_seats.py moves the models.
 * <ul>
 *   <li>{@link #FRONT}: on the arm's front face, the block in front of the arm. The arm's face is
 *       10 px into its block, so the block moves back 10 px.</li>
 *   <li>{@link #BACK}: on the arm's back face, behind a segment away from the pole. The face is at
 *       the block boundary already.</li>
 *   <li>{@link #END_LEFT} / {@link #END_RIGHT}: on an end of the arm, the arm running away from the
 *       block's back. The arm's centre line is 5 px towards its pole from the block's centre, to the
 *       block's left or right.</li>
 * </ul>
 * Every seat also lifts the block to the arm's height, by an amount that depends on the block's model.
 */
public enum ArmSeat implements IStringSerializable
{
    NONE("none"),
    FRONT("front"),
    BACK("back"),
    END_LEFT("end_left"),
    END_RIGHT("end_right");

    public static final double FRONT_GAP = 10 / 16.0;
    public static final double END_SHIFT = 5 / 16.0;

    private final String name;

    ArmSeat(String name)
    {
        this.name = name;
    }

    /** @return the seat of a block at {@code pos} facing {@code facing}, from the crossarm behind it if any */
    public static ArmSeat of(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        IBlockState behind = world.getBlockState(pos.offset(facing.getOpposite()));
        if (!(behind.getBlock() instanceof BlockCrossarm))
            return NONE;
        EnumFacing arm = behind.getValue(BlockCrossarm.FACING);
        if (arm == facing)
            return FRONT;
        if (arm == facing.getOpposite())
            return BACK;
        // The arm runs along the block's facing: its centre line is on its pole's side.
        return arm.getOpposite() == facing.rotateYCCW() ? END_LEFT : END_RIGHT;
    }

    /**
     * @param facing the block's facing
     * @param liftPx how far the block's model rises to meet the arm, in pixels
     * @return how far the block moves, in block units
     */
    public Vec3d offset(EnumFacing facing, double liftPx)
    {
        if (this == NONE)
            return Vec3d.ZERO;
        Vec3d lift = new Vec3d(0, liftPx / 16.0, 0);
        switch (this)
        {
            case FRONT:     return lift.add(step(facing.getOpposite(), FRONT_GAP));
            case END_LEFT:  return lift.add(step(facing.rotateYCCW(), END_SHIFT));
            case END_RIGHT: return lift.add(step(facing.rotateY(), END_SHIFT));
            default:        return lift;
        }
    }

    private static Vec3d step(EnumFacing way, double by)
    {
        return new Vec3d(way.getXOffset() * by, 0, way.getZOffset() * by);
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
