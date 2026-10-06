package com.micatechnologies.realgrid.blocks.insulators;

import com.micatechnologies.realgrid.blocks.crossarms.BlockCrossarm;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

/**
 * Where a top-mounted insulator sits: on its own ({@link #NONE}), or on a crossarm, moved back
 * towards the pole by {@link #SHIFT} so it stands on the arm's centre line. A crossarm is flush
 * against its pole at the back of its block, so the arm's centre is 5 px behind the block's.
 * scripts/gen_insulator_seats.py draws the moved models with the same shift.
 */
public enum InsulatorSeat implements IStringSerializable
{
    NONE("none", null),
    NORTH("north", EnumFacing.NORTH),
    EAST("east", EnumFacing.EAST),
    SOUTH("south", EnumFacing.SOUTH),
    WEST("west", EnumFacing.WEST);

    /** From the middle of a block to the middle of a crossarm: the arm spans 10-16 px of its block's depth. */
    public static final double SHIFT = 5 / 16.0;

    private final String name;
    @Nullable
    private final EnumFacing toward;

    InsulatorSeat(String name, @Nullable EnumFacing toward)
    {
        this.name = name;
        this.toward = toward;
    }

    /** @return the seat of an insulator at {@code pos}: on the crossarm below it, or none */
    public static InsulatorSeat at(IBlockAccess world, BlockPos pos)
    {
        IBlockState below = world.getBlockState(pos.down());
        if (!(below.getBlock() instanceof BlockCrossarm))
            return NONE;
        switch (below.getValue(BlockCrossarm.FACING).getOpposite())
        {
            case NORTH: return NORTH;
            case EAST:  return EAST;
            case SOUTH: return SOUTH;
            default:    return WEST;
        }
    }

    /** @return how far the insulator moves, in block units */
    public Vec3d shift()
    {
        if (toward == null)
            return Vec3d.ZERO;
        return new Vec3d(toward.getXOffset() * SHIFT, 0, toward.getZOffset() * SHIFT);
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
