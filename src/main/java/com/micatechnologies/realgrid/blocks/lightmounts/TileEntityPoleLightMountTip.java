package com.micatechnologies.realgrid.blocks.lightmounts;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/**
 * The tip block's tile: it only points Immersive Engineering at the mount. IE's wire coil and wire
 * cutters ask the clicked block for its {@link #getConnectionMaster connection master} and do
 * everything else with the master, so every wire lives on the mount's {@link TileEntityPoleLightMount}
 * and this tile never holds one.
 */
public class TileEntityPoleLightMountTip extends TileEntityImmersiveConnectable
{
    /** The mount, relative to this block. */
    private BlockPos toMount = BlockPos.ORIGIN;

    public void setMount(BlockPos mount)
    {
        toMount = mount.subtract(pos);
        markDirty();
    }

    public BlockPos getMountPos()
    {
        return pos.add(toMount);
    }

    @Override
    public BlockPos getConnectionMaster(WireType cableType, TargetingInfo target)
    {
        return getMountPos();
    }

    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target, Vec3i offset)
    {
        return false;
    }

    @Override
    public void connectCable(WireType cableType, TargetingInfo target, IImmersiveConnectable other)
    {
    }

    @Override
    public WireType getCableLimiter(TargetingInfo target)
    {
        return null;
    }

    @Override
    public Vec3d getConnectionOffset(Connection con)
    {
        return new Vec3d(0.5, 0.5, 0.5);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setInteger("mountX", toMount.getX());
        nbt.setInteger("mountY", toMount.getY());
        nbt.setInteger("mountZ", toMount.getZ());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        toMount = new BlockPos(nbt.getInteger("mountX"), nbt.getInteger("mountY"), nbt.getInteger("mountZ"));
    }
}
