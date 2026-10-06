package com.micatechnologies.realgrid.blocks.insulators;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.ICacheData;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Set;

/**
 * Base tile entity for all insulator variants. Acts as a wire relay: energy passes through
 * and multiple connections of the same wire type are allowed. Accepts LV / MV / HV.
 *
 * Shape and wire-attachment point come from an {@link InsulatorGeometry} supplied by the
 * subclass's no-arg constructor. The {@link #colorVariant} field is always present so
 * color-variant blocks can share this base without a separate class tier.
 */
public abstract class TileEntityInsulatorBase extends TileEntityImmersiveConnectable
    implements IDirectionalTile, IBlockBounds, ICacheData
{
    public EnumFacing facing = EnumFacing.NORTH;
    public int colorVariant = 0;
    protected int wireCount = 0;

    protected final InsulatorGeometry geometry;

    protected TileEntityInsulatorBase(InsulatorGeometry geometry)
    {
        this.geometry = geometry;
    }

    @Override protected boolean canTakeLV() { return true; }
    @Override protected boolean canTakeMV() { return true; }
    @Override protected boolean canTakeHV() { return true; }
    @Override protected boolean isRelay()   { return true; }

    @Override public boolean canConnect()      { return true; }
    @Override public boolean isEnergyOutput()  { return false; }
    @Override public int outputEnergy(int amount, boolean simulate, int energyType) { return 0; }
    @Override public boolean allowEnergyToPass(Connection con) { return true; }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setInteger("facing", facing.ordinal());
        nbt.setInteger("wireCount", wireCount);
        nbt.setInteger("colorVariant", colorVariant);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        EnumFacing loaded = EnumFacing.byIndex(nbt.getInteger("facing"));
        facing = loaded.getAxis() != EnumFacing.Axis.Y ? loaded : EnumFacing.NORTH;
        wireCount = nbt.getInteger("wireCount");
        colorVariant = nbt.getInteger("colorVariant");
    }

    /**
     * IE's wire coil asks this three-argument form, and TileEntityImmersiveConnectable answers it
     * from the LV/MV/HV tier alone without consulting the two-argument form, so the rules below
     * are only applied because this sends the question on to them.
     */
    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target, Vec3i offset)
    {
        return canConnectCable(cableType, target);
    }

    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target)
    {
        if (cableType == WireType.STRUCTURE_ROPE || cableType == WireType.STRUCTURE_STEEL || cableType == WireType.REDSTONE)
            return false;
        if (cableType == WireType.STEEL && !canTakeHV())
            return false;
        if (cableType == WireType.ELECTRUM && !canTakeMV())
            return false;
        if (cableType == WireType.COPPER && !canTakeLV())
            return false;
        return limitType == null || limitType == cableType;
    }

    @Override
    public void connectCable(WireType cableType, TargetingInfo target, IImmersiveConnectable other)
    {
        if (this.limitType == null)
            this.limitType = cableType;
        // IE registers the connection before telling either end, so the live count already includes it.
        wireCount = liveWireCount();
        markDirtyAndNotify();
    }

    @Override
    public void validate()
    {
        super.validate();
        if (world != null && !world.isRemote)
            ApiUtils.addFutureServerTask(world, this::recountWires);
    }

    /**
     * Takes both the wire count and the wire type from the connections IE actually holds for this block. The stored
     * pair used to drift: a pasted insulator keeps the values of the one it was copied from, wires cleaned up after a
     * world edit never told it, and the IE build this pack uses once reported every removed wire twice. A stale count
     * turns away cable the insulator should take.
     *
     * <p>A stale type is worse, and it is why the Engineer's Wire Cutters worked on some insulators and not others.
     * The cutters ask {@link #getCableLimiter(TargetingInfo)} for the type to cut and give up without touching
     * anything when the answer is null, and they only cut wires whose type matches the answer. An insulator holding
     * wires but no recorded type -- or the wrong one -- was therefore impossible to cut, permanently, while the
     * insulator beside it cut normally. Recovering the type from the wires themselves repairs those.
     */
    private void recountWires()
    {
        if (world == null || world.isRemote || isInvalid())
            return;
        Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
        int actual = conns == null ? 0 : conns.size();
        // Insulators hold a single type, so any attached wire names it. No wires means no type.
        WireType actualType = null;
        if (conns != null)
            for (Connection c : conns)
            {
                actualType = c.cableType;
                break;
            }

        if (actual == wireCount && actualType == limitType)
            return;
        wireCount = actual;
        limitType = actualType;
        markDirty();
    }

    private int liveWireCount()
    {
        if (world == null)
            return wireCount;
        Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
        return conns == null ? 0 : conns.size();
    }

    /**
     * The Engineer's Wire Cutters ask this for the type to cut and do nothing at all when the answer is null, so an
     * insulator that lost its recorded type became impossible to cut. {@link #recountWires()} repairs that, but only
     * when the chunk next loads; answering from the live wires in the meantime means the cutters work on the spot.
     */
    @Override
    public WireType getCableLimiter(TargetingInfo target)
    {
        if (limitType != null || world == null || world.isRemote)
            return limitType;
        Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
        if (conns != null)
            for (Connection c : conns)
                return c.cableType;
        return null;
    }

    @Override
    public void removeCable(Connection connection)
    {
        // A null connection is IE's "clearing everything" call, made before the connections are gone. Otherwise the
        // wire has already left IE's tables, so the live count is the truth.
        wireCount = connection == null ? 0 : liveWireCount();
        if (wireCount <= 0)
        {
            wireCount = 0;
            limitType = null;
        }
        markDirtyAndNotify();
    }

    private void markDirtyAndNotify()
    {
        this.markDirty();
        if (world != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public Vec3d getRaytraceOffset(IImmersiveConnectable link)
    {
        return new Vec3d(0.5, 0.5, 0.5);
    }

    @Override
    public Vec3d getConnectionOffset(Connection con)
    {
        return geometry.connectionOffset(facing).add(seat().shift());
    }

    @Override
    public float[] getBlockBounds()
    {
        float[] b = geometry.blockBounds(facing);
        Vec3d s = seat().shift();
        if (s == Vec3d.ZERO)
            return b;
        return new float[]{(float) (b[0] + s.x), b[1], (float) (b[2] + s.z), (float) (b[3] + s.x), b[4], (float) (b[5] + s.z)};
    }

    /**
     * Where this insulator sits. A top-mounted one standing on a crossarm is drawn moved back onto the
     * arm's centre line, so its wire point and box move with it; anything else stays where its
     * geometry puts it.
     */
    protected InsulatorSeat seat()
    {
        if (!geometry.isTopMount() || world == null)
            return InsulatorSeat.NONE;
        return InsulatorSeat.at(world, pos);
    }

    /**
     * Called by {@link BlockInsulatorBase#breakBlock} before the TileEntity is
     * removed from the world. Tears down all IE wire connections at this
     * position, drops wire coils at remote endpoints, and fires client render
     * events so orphaned wire segments disappear immediately.
     */
    public void onBlockDestroyed()
    {
        if (world == null || world.isRemote) return;

        ImmersiveNetHandler.INSTANCE.clearAllConnectionsFor(pos, world, true);

        // Defensive reset
        wireCount = 0;
        limitType = null;
    }

    // === ICacheData ===

    @Override
    public Object[] getCacheData()
    {
        return new Object[]{ getClass().getName() };
    }

    // === IDirectionalTile ===

    @Override public EnumFacing getFacing() { return facing; }
    @Override public void setFacing(EnumFacing facing) { this.facing = facing; }
    @Override public int getFacingLimitation() { return 2; }
    @Override public boolean mirrorFacingOnPlacement(EntityLivingBase placer) { return false; }

    @Override
    public boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase entity)
    {
        return true;
    }

    @Override
    public boolean canRotate(EnumFacing axis)
    {
        return true;
    }
}
