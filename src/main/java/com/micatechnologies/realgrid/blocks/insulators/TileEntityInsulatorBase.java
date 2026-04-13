package com.micatechnologies.realgrid.blocks.insulators;

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
        wireCount++;
        markDirtyAndNotify();
    }

    @Override
    public WireType getCableLimiter(TargetingInfo target)
    {
        return limitType;
    }

    @Override
    public void removeCable(Connection connection)
    {
        WireType type = connection != null ? connection.cableType : null;
        if (type == null)
            wireCount = 0;
        else
            wireCount--;
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
        return geometry.connectionOffset(facing);
    }

    @Override
    public float[] getBlockBounds()
    {
        return geometry.blockBounds(facing);
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
