package com.micatechnologies.realgrid.blocks.transformers;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHasDummyBlocks;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

/**
 * Abstract base class for all Real Grid Addon transformers.
 * Two blocks tall (dummy=0 base, dummy=1 upper).
 * Connection points:
 * - Index 0: invisible MV/LV relay point on top center (accepts multiple connections)
 * - Index 1: first HV connection (STEEL only)
 * - Index 2: second HV connection (STEEL only, 2-wire variants only)
 */
public abstract class TileEntityRealTransformer extends TileEntityImmersiveConnectable
    implements IDirectionalTile, IHasDummyBlocks, IBlockBounds
{
    public EnumFacing facing = EnumFacing.NORTH;
    public int dummy = 0;

    // HV cable tracking
    protected WireType hvCable1 = null;
    protected WireType hvCable2 = null;

    // MV/LV relay tracking - counts how many MV/LV connections exist
    protected int mvLvCableCount = 0;
    protected WireType mvLvLimitType = null;

    /**
     * @return true if this transformer has 2 HV connection points
     */
    public abstract boolean isTwoWire();

    /**
     * @return true if HV connections are on the side (Class A) vs top (Class C)
     */
    public abstract boolean isHvOnSide();

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
    public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setInteger("facing", facing.ordinal());
        nbt.setInteger("dummy", dummy);
        if (hvCable1 != null)
            nbt.setString("hvCable1", hvCable1.getUniqueName());
        if (hvCable2 != null)
            nbt.setString("hvCable2", hvCable2.getUniqueName());
        nbt.setInteger("mvLvCableCount", mvLvCableCount);
        if (mvLvLimitType != null)
            nbt.setString("mvLvLimitType", mvLvLimitType.getUniqueName());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        facing = EnumFacing.byIndex(nbt.getInteger("facing"));
        dummy = nbt.getInteger("dummy");
        hvCable1 = nbt.hasKey("hvCable1") ? ApiUtils.getWireTypeFromNBT(nbt, "hvCable1") : null;
        hvCable2 = nbt.hasKey("hvCable2") ? ApiUtils.getWireTypeFromNBT(nbt, "hvCable2") : null;
        mvLvCableCount = nbt.getInteger("mvLvCableCount");
        mvLvLimitType = nbt.hasKey("mvLvLimitType") ? ApiUtils.getWireTypeFromNBT(nbt, "mvLvLimitType") : null;
    }

    @Override
    public BlockPos getConnectionMaster(WireType cableType, TargetingInfo target)
    {
        return getPos().add(0, -dummy, 0);
    }

    /**
     * Determines which connection point is targeted based on click position.
     * @return 0 for invisible MV/LV relay, 1 for first HV, 2 for second HV
     */
    public int getTargetedConnector(TargetingInfo target)
    {
        if (isHvOnSide())
        {
            // Class A: HV on sides, MV/LV on top center
            // If clicking on the upper portion (top center area), target the MV/LV relay
            if (target.hitY > 0.75)
            {
                return 0; // MV/LV relay point
            }
            // Otherwise, determine left or right side for HV
            if (facing == EnumFacing.NORTH)
            {
                if (target.hitX < 0.5)
                    return 1;
                else
                    return isTwoWire() ? 2 : 1;
            }
            else if (facing == EnumFacing.SOUTH)
            {
                if (target.hitX < 0.5)
                    return isTwoWire() ? 2 : 1;
                else
                    return 1;
            }
            else if (facing == EnumFacing.WEST)
            {
                if (target.hitZ < 0.5)
                    return isTwoWire() ? 2 : 1;
                else
                    return 1;
            }
            else // EAST
            {
                if (target.hitZ < 0.5)
                    return 1;
                else
                    return isTwoWire() ? 2 : 1;
            }
        }
        else
        {
            // Class C: HV on top, MV/LV also on top but center
            // Use horizontal position to determine target
            if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
            {
                if (target.hitX > 0.35 && target.hitX < 0.65)
                    return 0; // center = MV/LV relay
                if (facing == EnumFacing.NORTH)
                {
                    if (target.hitX < 0.35)
                        return 1;
                    else
                        return isTwoWire() ? 2 : 1;
                }
                else
                {
                    if (target.hitX < 0.35)
                        return isTwoWire() ? 2 : 1;
                    else
                        return 1;
                }
            }
            else
            {
                if (target.hitZ > 0.35 && target.hitZ < 0.65)
                    return 0; // center = MV/LV relay
                if (facing == EnumFacing.WEST)
                {
                    if (target.hitZ < 0.35)
                        return isTwoWire() ? 2 : 1;
                    else
                        return 1;
                }
                else
                {
                    if (target.hitZ < 0.35)
                        return 1;
                    else
                        return isTwoWire() ? 2 : 1;
                }
            }
        }
    }

    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target)
    {
        if (cableType == WireType.STRUCTURE_ROPE || cableType == WireType.STRUCTURE_STEEL || cableType == WireType.REDSTONE)
            return false;

        if (dummy != 0)
        {
            TileEntity master = world.getTileEntity(getPos().add(0, -dummy, 0));
            return master instanceof TileEntityRealTransformer
                && ((TileEntityRealTransformer) master).canConnectCable(cableType, target);
        }

        int tc = getTargetedConnector(target);
        switch (tc)
        {
            case 0:
                // MV/LV relay point - accepts COPPER or ELECTRUM, multiple connections
                if (cableType != WireType.COPPER && cableType != WireType.ELECTRUM)
                    return false;
                // If there's already a limit type, enforce the same type (relay-style)
                return mvLvLimitType == null || mvLvLimitType == cableType;
            case 1:
                // First HV connection - STEEL only
                if (cableType != WireType.STEEL)
                    return false;
                return hvCable1 == null;
            case 2:
                // Second HV connection - STEEL only (2-wire only)
                if (!isTwoWire())
                    return false;
                if (cableType != WireType.STEEL)
                    return false;
                return hvCable2 == null;
            default:
                return false;
        }
    }

    @Override
    public void connectCable(WireType cableType, TargetingInfo target, IImmersiveConnectable other)
    {
        if (dummy != 0)
        {
            TileEntity master = world.getTileEntity(getPos().add(0, -dummy, 0));
            if (master instanceof TileEntityRealTransformer)
                ((TileEntityRealTransformer) master).connectCable(cableType, target, other);
            return;
        }

        int tc = getTargetedConnector(target);
        switch (tc)
        {
            case 0:
                mvLvCableCount++;
                if (mvLvLimitType == null)
                    mvLvLimitType = cableType;
                break;
            case 1:
                hvCable1 = cableType;
                break;
            case 2:
                hvCable2 = cableType;
                break;
        }
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
        int tc = getTargetedConnector(target);
        switch (tc)
        {
            case 0:
                return mvLvLimitType;
            case 1:
                return hvCable1;
            case 2:
                return hvCable2;
        }
        return null;
    }

    @Override
    public void removeCable(Connection connection)
    {
        WireType type = connection != null ? connection.cableType : null;
        if (type == null)
        {
            hvCable1 = null;
            hvCable2 = null;
            mvLvCableCount = 0;
            mvLvLimitType = null;
        }
        else if (type == WireType.STEEL)
        {
            // Remove HV cable - try hvCable1 first, then hvCable2
            if (hvCable1 != null)
                hvCable1 = null;
            else if (hvCable2 != null)
                hvCable2 = null;
        }
        else
        {
            // Remove MV/LV cable
            mvLvCableCount--;
            if (mvLvCableCount <= 0)
            {
                mvLvCableCount = 0;
                mvLvLimitType = null;
            }
        }

        // Another double inventory block fix
        this.markDirty();
        if (world != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 2);
        }
    }

    @Override
    public Vec3d getRaytraceOffset(IImmersiveConnectable link)
    {
        return new Vec3d(0.5, 1.5, 0.5);
    }

    @Override
    public Vec3d getConnectionOffset(Connection con)
    {
        if (con.cableType == WireType.STEEL)
        {
            // HV connections
            return getHvConnectionOffset(con);
        }
        else
        {
            // MV/LV connection - invisible relay on top center
            return new Vec3d(0.5, 2.0, 0.5);
        }
    }

    /**
     * Gets the 3D offset for an HV connection. Subclasses may override for fine-tuning.
     */
    protected Vec3d getHvConnectionOffset(Connection con)
    {
        if (isHvOnSide())
        {
            // Class A: HV on the sides of the upper block
            // Determine if this is the first or second HV cable
            boolean isFirst = (hvCable1 != null && con.cableType == hvCable1);
            double sideOffset = isFirst ? 0.125 : 0.875;

            if (facing == EnumFacing.NORTH)
                return new Vec3d(sideOffset, 1.5, 0.5);
            else if (facing == EnumFacing.SOUTH)
                return new Vec3d(1.0 - sideOffset, 1.5, 0.5);
            else if (facing == EnumFacing.WEST)
                return new Vec3d(0.5, 1.5, 1.0 - sideOffset);
            else // EAST
                return new Vec3d(0.5, 1.5, sideOffset);
        }
        else
        {
            // Class C: HV on the top of the upper block
            boolean isFirst = (hvCable1 != null && con.cableType == hvCable1);
            double sideOffset = isFirst ? 0.1875 : 0.8125;

            if (facing == EnumFacing.NORTH)
                return new Vec3d(sideOffset, 2.0, 0.5);
            else if (facing == EnumFacing.SOUTH)
                return new Vec3d(1.0 - sideOffset, 2.0, 0.5);
            else if (facing == EnumFacing.WEST)
                return new Vec3d(0.5, 2.0, 1.0 - sideOffset);
            else // EAST
                return new Vec3d(0.5, 2.0, sideOffset);
        }
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
        return false;
    }

    @Override
    public boolean canRotate(EnumFacing axis)
    {
        return false;
    }

    // === IHasDummyBlocks ===

    @Override
    public boolean isDummy()
    {
        return dummy != 0;
    }

    @Override
    public void placeDummies(BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        // Place the upper dummy block
        world.setBlockState(pos.up(), state);
        TileEntity te = world.getTileEntity(pos.up());
        if (te instanceof TileEntityRealTransformer)
        {
            ((TileEntityRealTransformer) te).dummy = 1;
            ((TileEntityRealTransformer) te).facing = this.facing;
        }
    }

    @Override
    public void breakDummies(BlockPos pos, IBlockState state)
    {
        // Remove both blocks of the 2-tall structure
        for (int i = 0; i <= 1; i++)
        {
            world.setBlockToAir(getPos().add(0, -dummy, 0).add(0, i, 0));
        }
    }

    // === IBlockBounds ===

    @Override
    public float[] getBlockBounds()
    {
        if (dummy == 1)
        {
            // Upper block - slightly smaller
            return new float[]{0.0625f, 0, 0.0625f, 0.9375f, 0.875f, 0.9375f};
        }
        // Base block - full size
        return new float[]{0, 0, 0, 1, 1, 1};
    }

    @Override
    public Set<BlockPos> getIgnored(IImmersiveConnectable other)
    {
        return ImmutableSet.of(pos, pos.up());
    }

    /**
     * Called by {@link BlockRealTransformerBase#breakBlock} before the TileEntity
     * is removed from the world. Tears down all IE wire connections at this
     * position via {@link ImmersiveNetHandler#clearAllConnectionsFor}.
     *
     * <p>{@code clearAllConnectionsFor(pos, world, true)} removes every
     * connection referencing this position, calls {@code removeCable()} on all
     * remote endpoints, drops wire coils (doDrops=true), and fires client render
     * events so orphaned wire segments disappear immediately.
     */
    public void onBlockDestroyed()
    {
        if (world == null || world.isRemote) return;

        ImmersiveNetHandler.INSTANCE.clearAllConnectionsFor(pos, world, true);

        // Defensive reset -- clearAllConnectionsFor already triggers
        // removeCable(null) which resets state, but guard here as well.
        hvCable1 = null;
        hvCable2 = null;
        mvLvCableCount = 0;
        mvLvLimitType = null;
    }
}