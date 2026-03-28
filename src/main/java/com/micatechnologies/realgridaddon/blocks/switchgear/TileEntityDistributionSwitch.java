package com.micatechnologies.realgridaddon.blocks.switchgear;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IRedstoneOutput;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Distribution Switchgear (MacLean Triple Action Air Release Switch)
 * Supports up to 3 wire connections (any mix of HV, MV, LV).
 * Controlled by redstone signal - when powered, switch opens (stops power) unless inverted.
 * When no redstone, switch closes (allows power) unless inverted.
 * Invertible with engineer's hammer + sneak.
 */
public class TileEntityDistributionSwitch extends TileEntityImmersiveConnectable
    implements IDirectionalTile, IBlockBounds, IRedstoneOutput, ITickable
{
    public static final int MAX_WIRES = 3;

    public EnumFacing facing = EnumFacing.NORTH;
    public int wires = 0;
    public boolean active = true; // true = switch closed (power flows)
    public boolean inverted = false;

    @Override
    protected boolean canTakeLV()
    {
        return true;
    }

    @Override
    protected boolean canTakeMV()
    {
        return true;
    }

    @Override
    protected boolean canTakeHV()
    {
        return true;
    }

    @Override
    protected boolean isRelay()
    {
        return false;
    }

    @Override
    public boolean canConnect()
    {
        return true;
    }

    @Override
    public boolean isEnergyOutput()
    {
        return false;
    }

    @Override
    public int outputEnergy(int amount, boolean simulate, int energyType)
    {
        return 0;
    }

    @Override
    public boolean allowEnergyToPass(Connection con)
    {
        return active;
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            boolean redstonePowered = isRedstonePowered();
            // When powered by redstone: opens switch (stops power) unless inverted
            boolean shouldBeActive;
            if (inverted)
            {
                // Inverted: redstone signal = allow power
                shouldBeActive = redstonePowered;
            }
            else
            {
                // Normal: no redstone = allow power
                shouldBeActive = !redstonePowered;
            }

            if (shouldBeActive != active)
            {
                active = shouldBeActive;
                if (wires > 1)
                {
                    ImmersiveNetHandler.INSTANCE.resetCachedIndirectConnections();
                }
                notifyNeighbours();
                this.markDirty();
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
                world.addBlockEvent(getPos(), getBlockType(), active ? 1 : 0, 0);
            }
        }
    }

    private boolean isRedstonePowered()
    {
        for (EnumFacing side : EnumFacing.VALUES)
        {
            if (world.getRedstonePower(pos.offset(side), side) > 0)
                return true;
        }
        return false;
    }

    public void notifyNeighbours()
    {
        markDirty();
        world.notifyNeighborsOfStateChange(getPos(), getBlockType(), true);
        for (EnumFacing f : EnumFacing.VALUES)
        {
            world.notifyNeighborsOfStateChange(getPos().offset(f), getBlockType(), true);
        }
    }

    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target)
    {
        if (cableType != null && !cableType.isEnergyWire())
            return false;
        if (cableType == WireType.STEEL && !canTakeHV())
            return false;
        if (cableType == WireType.ELECTRUM && !canTakeMV())
            return false;
        if (cableType == WireType.COPPER && !canTakeLV())
            return false;
        if (wires >= MAX_WIRES)
            return false;
        return limitType == null || cableType == limitType;
    }

    @Override
    public void connectCable(WireType cableType, TargetingInfo target, IImmersiveConnectable other)
    {
        if (this.limitType == null)
            this.limitType = cableType;
        wires++;
        this.markDirty();
        if (world != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
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
            wires = 0;
        else
            wires--;
        if (wires <= 0)
        {
            wires = 0;
            limitType = null;
        }
        this.markDirty();
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
        nbt.setInteger("facing", facing.ordinal());
        nbt.setInteger("wires", wires);
        nbt.setBoolean("active", active);
        nbt.setBoolean("inverted", inverted);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        facing = EnumFacing.getFront(nbt.getInteger("facing"));
        wires = nbt.getInteger("wires");
        active = nbt.getBoolean("active");
        inverted = nbt.getBoolean("inverted");
    }

    @Override
    public Vec3d getRaytraceOffset(IImmersiveConnectable link)
    {
        return new Vec3d(0.5, 0.5, 0.5);
    }

    @Override
    public Vec3d getConnectionOffset(Connection con)
    {
        return new Vec3d(0.5, 0.5, 0.5);
    }

    @Override
    public boolean receiveClientEvent(int id, int arg)
    {
        if (super.receiveClientEvent(id, arg))
            return true;
        this.active = id == 1;
        if (world != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
        return true;
    }

    // === IDirectionalTile ===

    @Override
    public EnumFacing getFacing()
    {
        return facing;
    }

    @Override
    public void setFacing(EnumFacing facing)
    {
        this.facing = facing;
    }

    @Override
    public int getFacingLimitation()
    {
        return 2; // Horizontal only
    }

    @Override
    public boolean mirrorFacingOnPlacement(EntityLivingBase placer)
    {
        return true;
    }

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

    // === IBlockBounds ===

    @Override
    public float[] getBlockBounds()
    {
        return new float[]{0.1875f, 0.125f, 0.1875f, 0.8125f, 0.875f, 0.8125f};
    }

    // === IRedstoneOutput ===

    @Override
    public int getWeakRSOutput(IBlockState state, EnumFacing side)
    {
        return (active ^ inverted) ? 15 : 0;
    }

    @Override
    public int getStrongRSOutput(IBlockState state, EnumFacing side)
    {
        return side.getOpposite() == facing && (active ^ inverted) ? 15 : 0;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, EnumFacing side)
    {
        return true;
    }
}
