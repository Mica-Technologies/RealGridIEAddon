package com.micatechnologies.realgrid.blocks.lightmounts;

import com.micatechnologies.realgrid.util.BoundsUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

/**
 * Where a pole light mount's tip insulator, tip block and fixture are, for any facing. The numbers
 * come from {@link PoleLightMountTips}, which scripts/gen_pole_light_mounts.py writes from the same
 * arm it draws, so the wire point always lands on the insulator in the model.
 */
public final class PoleLightMountGeometry
{
    private PoleLightMountGeometry() {}

    private static float[] row(int length, ArmSwing swing)
    {
        return PoleLightMountTips.TIPS[length - 1][swing.ordinal()];
    }

    /** Turns a north-facing block offset to face the given way. */
    private static BlockPos offset(BlockPos from, float[] row, int at, EnumFacing facing)
    {
        int dx = Math.round(row[at]), dy = Math.round(row[at + 1]), dz = Math.round(row[at + 2]);
        switch (facing)
        {
            case SOUTH: return from.add(-dx, dy, -dz);
            case EAST:  return from.add(-dz, dy, dx);
            case WEST:  return from.add(dz, dy, -dx);
            default:    return from.add(dx, dy, dz);
        }
    }

    /** @return the block holding the tip insulator of a mount at {@code mount} */
    public static BlockPos tipPos(BlockPos mount, int length, ArmSwing swing, EnumFacing facing)
    {
        return offset(mount, row(length, swing), PoleLightMountTips.TIP_BLOCK, facing);
    }

    /**
     * @return the block the light goes in, for a mount at {@code mount}: the CSM street light or the
     *         standing floodlight one block up and out, a hanging floodlight the block below that
     */
    public static BlockPos fixturePos(BlockPos mount, int length, ArmSwing swing, EnumFacing facing, LightStyle style)
    {
        BlockPos street = offset(mount, row(length, swing), PoleLightMountTips.FIXTURE_BLOCK, facing);
        return style == LightStyle.FLOOD_DOWN ? street.down() : street;
    }

    /**
     * @param side which side the insulator stands out of, where the arm mounts it on a side (the
     *             5-block arm); ignored where it stands on top
     * @return where wires attach, on top of the tip insulator, relative to the mount block
     */
    public static Vec3d wirePoint(int length, ArmSwing swing, EnumFacing facing, InsulatorSide side)
    {
        float[] r = row(length, swing);
        int w = side == InsulatorSide.LEFT ? PoleLightMountTips.WIRE_LEFT : PoleLightMountTips.WIRE;
        return BoundsUtil.rotateOffset(new Vec3d(r[w], r[w + 1], r[w + 2]), facing);
    }

    /** @return whether this arm's tip insulator stands out of one side rather than on top */
    public static boolean isSideMounted(int length, ArmSwing swing)
    {
        float[] r = row(length, swing);
        int w = PoleLightMountTips.WIRE, l = PoleLightMountTips.WIRE_LEFT;
        return r[w] != r[l] || r[w + 1] != r[l + 1] || r[w + 2] != r[l + 2];
    }

    /** @return the tip block's box round the insulator, in the tip block's own coordinates */
    public static AxisAlignedBB tipBox(int length, ArmSwing swing, EnumFacing facing)
    {
        float[] r = row(length, swing);
        float[] north = new float[6];
        System.arraycopy(r, PoleLightMountTips.TIP_BOX, north, 0, 6);
        float[] b = BoundsUtil.rotateBounds(north, facing);
        return new AxisAlignedBB(b[0], b[1], b[2], b[3], b[4], b[5]);
    }

    /** @return the tip block of the mount in this state at {@code mount}, or null if it is not a mount */
    @Nullable
    public static BlockPos tipPos(IBlockState mountState, BlockPos mount)
    {
        if (!(mountState.getBlock() instanceof BlockPoleLightMount))
            return null;
        return tipPos(mount, ((BlockPoleLightMount) mountState.getBlock()).getArmLength(),
            mountState.getValue(BlockPoleLightMount.SWING), mountState.getValue(BlockPoleLightMount.FACING));
    }
}
