package com.micatechnologies.realgridaddon.blocks.insulators;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

/**
 * Base tile entity for all insulator types.
 * Acts as a wire relay: allows energy to pass through and supports multiple connections of the same wire type.
 * Accepts LV, MV, and HV wires.
 */
public class TileEntityInsulatorBase extends TileEntityImmersiveConnectable
    implements IDirectionalTile, IBlockBounds
{
    public EnumFacing facing = EnumFacing.NORTH;
    protected int wireCount = 0;

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
        return true;
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
        return true;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setInteger("facing", facing.ordinal());
        nbt.setInteger("wireCount", wireCount);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        facing = EnumFacing.byIndex(nbt.getInteger("facing"));
        wireCount = nbt.getInteger("wireCount");
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
        // Relay-style: allow multiple connections of the same type
        return limitType == null || limitType == cableType;
    }

    @Override
    public void connectCable(WireType cableType, TargetingInfo target, IImmersiveConnectable other)
    {
        if (this.limitType == null)
            this.limitType = cableType;
        wireCount++;
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
        {
            wireCount = 0;
        }
        else
        {
            wireCount--;
        }
        if (wireCount <= 0)
        {
            wireCount = 0;
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
    public Vec3d getRaytraceOffset(IImmersiveConnectable link)
    {
        return new Vec3d(0.5, 0.5, 0.5);
    }

    @Override
    public Vec3d getConnectionOffset(Connection con)
    {
        return new Vec3d(0.5, 0.75, 0.5);
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
        return false;
    }

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

    // === IBlockBounds ===

    @Override
    public float[] getBlockBounds()
    {
        // Insulator shape: smaller than full block
        return new float[]{0.25f, 0.0f, 0.25f, 0.75f, 0.875f, 0.75f};
    }
}
