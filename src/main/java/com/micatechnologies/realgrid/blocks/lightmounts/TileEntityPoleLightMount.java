package com.micatechnologies.realgrid.blocks.lightmounts;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import com.micatechnologies.realgrid.blocks.insulators.InsulatorGeometry;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityInsulatorBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

/**
 * The wire point on a pole light mount's bracket. Shared by every arm length: the bracket, and so
 * the wire point, is the same on all of them.
 *
 * <p>It takes Steel Cable, which carries a light's messenger wire to it, or LV copper, which can
 * feed an Immersive Engineering floodlight hung at the arm. One type at a time, several wires of
 * it, passing energy through like the insulators. The wire-count and wire-type bookkeeping, and
 * its repair from IE's live connections, all come from {@link TileEntityInsulatorBase}, which also
 * routes IE's three-argument canConnectCable to the rules below.
 */
public class TileEntityPoleLightMount extends TileEntityInsulatorBase
{
    /**
     * The front face of the guide insulator, for a north-facing mount. Must agree with
     * {@code guide_insulator.obj}, which scripts/gen_pole_light_mounts.py generates.
     */
    static final Vec3d WIRE_POINT = new Vec3d(0.5, 0.875, 0.75);

    /** The bracket, against the pole on the south face. The arm overhangs other blocks and has no box. */
    static final float[] BRACKET_BOUNDS = {0.25f, 0.0f, 0.6875f, 0.75f, 1.0f, 1.0f};

    static final InsulatorGeometry GEOMETRY = InsulatorGeometry.rotatable(WIRE_POINT, BRACKET_BOUNDS);

    public TileEntityPoleLightMount()
    {
        super(GEOMETRY);
    }

    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target)
    {
        if (cableType != WireType.STRUCTURE_STEEL && cableType != WireType.COPPER)
            return false;
        return limitType == null || limitType == cableType;
    }

    /** @return true once a wire is attached, which is when the guide insulator is drawn */
    public boolean isWired()
    {
        return wireCount > 0;
    }

    /**
     * The wire point follows the block state, not the inherited {@code facing} field. The field is
     * only written when a player places the block, so a mount made by a command, a structure paste
     * or a world edit would otherwise hang its wires as if it faced north.
     */
    @Override
    public Vec3d getConnectionOffset(Connection con)
    {
        return GEOMETRY.connectionOffset(stateFacing());
    }

    @Override
    public float[] getBlockBounds()
    {
        return GEOMETRY.blockBounds(stateFacing());
    }

    private EnumFacing stateFacing()
    {
        if (world != null)
        {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof BlockPoleLightMount)
                return state.getValue(BlockPoleLightMount.FACING);
        }
        return facing;
    }

    /**
     * IE caches each baked wire model under the block state's property values and this data, but
     * not the block. Every arm length shares this class and the same properties, so without the
     * block here a 3-block arm and a 4-block arm with the same facing and swing share one cache
     * entry, and whichever is drawn first is drawn for both.
     */
    @Override
    public Object[] getCacheData()
    {
        return new Object[]{ getClass().getName(), getBlockType() };
    }

    // The hammer swings the arm instead (see BlockPoleLightMount), so it never turns the mount.

    @Override
    public boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase entity)
    {
        return false;
    }

    @Override
    public boolean canRotate(EnumFacing axis)
    {
        return false;
    }
}
