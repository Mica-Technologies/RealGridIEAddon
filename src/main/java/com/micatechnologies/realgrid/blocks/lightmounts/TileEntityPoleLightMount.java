package com.micatechnologies.realgrid.blocks.lightmounts;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import com.google.common.collect.ImmutableSet;
import com.micatechnologies.realgrid.blocks.insulators.InsulatorGeometry;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityInsulatorBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Set;
import javax.annotation.Nullable;

/**
 * A pole light mount's wire point, on the insulator at the tip of its arm next to the fixture.
 * Shared by every arm length; where the tip is comes from {@link PoleLightMountGeometry}. Wires
 * are attached by clicking the tip block ({@link BlockPoleLightMountTip}), which sends IE here;
 * clicks on the bracket are turned away.
 *
 * <p>It takes Steel Cable, which carries a light's messenger wire to it, or LV copper, which can
 * feed an Immersive Engineering floodlight hung at the arm. One type at a time, several wires of
 * it, passing energy through like the insulators. The wire-count and wire-type bookkeeping, and
 * its repair from IE's live connections, all come from {@link TileEntityInsulatorBase}, which also
 * routes IE's three-argument canConnectCable to the rules below.
 */
public class TileEntityPoleLightMount extends TileEntityInsulatorBase
{
    /** The bracket, against the pole on the south face. The arm overhangs other blocks and has no box. */
    static final float[] BRACKET_BOUNDS = {0.25f, 0.0f, 0.6875f, 0.75f, 1.0f, 1.0f};

    /**
     * Only the bracket's bounds are used from this; the wire point it carries is never asked for,
     * because {@link #getConnectionOffset} answers from the tip.
     */
    static final InsulatorGeometry GEOMETRY = InsulatorGeometry.rotatable(new Vec3d(0.5, 0.5, 0.85), BRACKET_BOUNDS);

    public TileEntityPoleLightMount()
    {
        super(GEOMETRY);
    }

    /**
     * IE passes the clicked block's offset from this one: zero is the bracket itself, which takes no
     * wires. Wires go on at the tip insulator.
     */
    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target, Vec3i offset)
    {
        if (offset == null || (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0))
            return false;
        return canConnectCable(cableType, target);
    }

    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target)
    {
        if (cableType != WireType.STRUCTURE_STEEL && cableType != WireType.COPPER)
            return false;
        return limitType == null || limitType == cableType;
    }

    /**
     * Mounts from before the tip block existed, or whose tip went missing (a world edit, a paste),
     * put it back once loaded, so their insulator can be clicked again. A tip in an unloaded chunk is
     * left for later rather than loading the chunk for it.
     */
    @Override
    public void onLoad()
    {
        super.onLoad();
        if (world != null && !world.isRemote)
            ApiUtils.addFutureServerTask(world, this::restoreTip);
    }

    private void restoreTip()
    {
        if (world == null || world.isRemote || isInvalid())
            return;
        IBlockState state = mountState();
        if (state == null)
            return;
        BlockPos tip = PoleLightMountGeometry.tipPos(state, pos);
        if (tip != null && world.isBlockLoaded(tip) && !BlockPoleLightMount.hasTip(world, pos, state))
            BlockPoleLightMount.placeTip(world, pos, state);
    }

    /** @return true once a wire is attached, which is when the slack loop is drawn */
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
        IBlockState state = mountState();
        if (state == null)
            return new Vec3d(0.5, 0.5, 0.5);
        return PoleLightMountGeometry.wirePoint(armLength(state), state.getValue(BlockPoleLightMount.SWING),
            state.getValue(BlockPoleLightMount.FACING));
    }

    /**
     * IE checks a new wire for blocks in its way, skipping only these. The wire leaves the tip
     * insulator, so the tip block and the fixture next to it are skipped as well as the mount.
     */
    @Override
    public Set<BlockPos> getIgnored(IImmersiveConnectable other)
    {
        IBlockState state = mountState();
        if (state == null)
            return ImmutableSet.of(pos);
        int length = armLength(state);
        ArmSwing swing = state.getValue(BlockPoleLightMount.SWING);
        EnumFacing facing = state.getValue(BlockPoleLightMount.FACING);
        return ImmutableSet.of(pos, PoleLightMountGeometry.tipPos(pos, length, swing, facing),
            PoleLightMountGeometry.fixturePos(pos, length, swing, facing));
    }

    private static int armLength(IBlockState state)
    {
        return ((BlockPoleLightMount) state.getBlock()).getArmLength();
    }

    /** @return this mount's block state, or null when the world doesn't hold a mount here */
    @Nullable
    private IBlockState mountState()
    {
        if (world == null)
            return null;
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof BlockPoleLightMount ? state : null;
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
