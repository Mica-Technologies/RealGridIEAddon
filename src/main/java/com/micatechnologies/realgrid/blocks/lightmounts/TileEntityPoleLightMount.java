package com.micatechnologies.realgrid.blocks.lightmounts;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFloodlight;
import com.google.common.collect.ImmutableSet;
import com.micatechnologies.realgrid.blocks.insulators.InsulatorGeometry;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityInsulatorBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
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
 * <p>It takes Steel Cable, which carries a light's messenger wire to it. LV copper, which feeds an
 * Immersive Engineering floodlight, is taken only when a 1- or 2-block mount is set up for a
 * floodlight ({@link LightStyle}) and the floodlight is in place; copper already attached is kept
 * either way. One type at a time, several wires of it, passing energy through like the insulators. The wire-count and wire-type bookkeeping, and
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

    /** What the arm carries; only the 1- and 2-block arms offer anything but a street light. */
    private LightStyle style = LightStyle.STREET;

    public TileEntityPoleLightMount()
    {
        super(GEOMETRY);
    }

    public LightStyle getStyle()
    {
        return style;
    }

    public void setStyle(LightStyle style)
    {
        this.style = style;
        markDirty();
        if (world != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setString("lightStyle", style.getName());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        style = LightStyle.byName(nbt.getString("lightStyle"));
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
        if (cableType != WireType.STRUCTURE_STEEL && !(cableType == WireType.COPPER && feedsFloodlight()))
            return false;
        return limitType == null || limitType == cableType;
    }

    /** @return whether this mount is set up for a floodlight and an IE floodlight is in its place */
    public boolean feedsFloodlight()
    {
        IBlockState state = mountState();
        if (state == null || !style.isFloodlight())
            return false;
        BlockPos light = PoleLightMountGeometry.fixturePos(pos, armLength(state), state.getValue(BlockPoleLightMount.SWING),
            state.getValue(BlockPoleLightMount.FACING), style);
        return world.getTileEntity(light) instanceof TileEntityFloodlight;
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
            state.getValue(BlockPoleLightMount.FACING), insulatorSide(con));
    }

    /**
     * Which side the tip insulator stands out of, on an arm that mounts it on a side: the side of the
     * tip most of its wires come from, seen looking along the arm from the pole. Every wire then
     * attaches to that one insulator. Measured from the tip, not the mount: a swung arm's tip is
     * blocks to one side of the pole. Arms with the insulator on top answer {@link InsulatorSide#RIGHT}.
     *
     * @param extra a wire to count as well, such as one IE is about to attach; may be null
     */
    public InsulatorSide insulatorSide(@Nullable Connection extra)
    {
        IBlockState state = mountState();
        if (state == null || !PoleLightMountGeometry.isSideMounted(armLength(state), state.getValue(BlockPoleLightMount.SWING)))
            return InsulatorSide.RIGHT;
        EnumFacing right = state.getValue(BlockPoleLightMount.FACING).rotateY();
        BlockPos tip = PoleLightMountGeometry.tipPos(state, pos);
        long across = 0;
        Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
        if (conns != null)
            for (Connection c : conns)
                across += across(c, tip, right);
        if (extra != null && (conns == null || !conns.contains(extra)))
            across += across(extra, tip, right);
        return across < 0 ? InsulatorSide.LEFT : InsulatorSide.RIGHT;
    }

    /** @return how far a wire's far end lies to the right of the tip (negative: to its left) */
    private long across(Connection c, BlockPos tip, EnumFacing right)
    {
        BlockPos far = pos.equals(c.start) ? c.end : c.start;
        return (long) (far.getX() - tip.getX()) * right.getXOffset() + (long) (far.getZ() - tip.getZ()) * right.getZOffset();
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
            PoleLightMountGeometry.fixturePos(pos, length, swing, facing, style));
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
