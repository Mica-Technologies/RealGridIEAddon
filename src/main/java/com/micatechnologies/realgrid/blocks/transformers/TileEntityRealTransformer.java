package com.micatechnologies.realgrid.blocks.transformers;

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
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHasDummyBlocks;
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
    implements IDirectionalTile, IHasDummyBlocks, IBlockBounds, ICacheData
{
    public EnumFacing facing = EnumFacing.NORTH;
    public int dummy = 0;

    // HV cable tracking
    protected WireType hvCable1 = null;
    protected WireType hvCable2 = null;

    // MV/LV relay tracking - counts how many MV/LV connections exist
    protected int mvLvCableCount = 0;
    protected WireType mvLvLimitType = null;

    // Attachment geometry taken from the block models, in block units. Each is the point the wire
    // should visually meet, so a model edit has to be reflected here.

    /** Class A arms: midpoint of the insulator stack pair on each overhanging arm. */
    protected static final double CLASS_A_ARM_LEFT = -0.21875;  // model X = -3.5px
    protected static final double CLASS_A_ARM_RIGHT = 1.21875;  // model X = 19.5px
    /** Class A arms sit on the upper block, model Y = 26px. */
    protected static final double CLASS_A_ARM_HEIGHT = 1.625;
    /** Class C bushing cap tops: upper block (1.0) plus model Y = 29px. */
    protected static final double CLASS_C_BUSHING_HEIGHT = 2.8125;
    /** Jumbo bushing cap tops: upper block (1.0) plus model Y = 32px. */
    protected static final double JUMBO_BUSHING_HEIGHT = 3.0;

    /**
     * @return true if this transformer has 2 HV connection points
     */
    public abstract boolean isTwoWire();

    /**
     * Whether HV attaches to arms that overhang the block's sides (Class A) rather than to bushings
     * rising from its top (Class C, Jumbo). This governs click regions only -- where the wire actually
     * lands comes from {@link #getHvBushingOffsets()} and {@link #getHvBushingHeight()}, so a variant
     * can never again inherit another model's attachment points by sharing this flag.
     *
     * @return true if HV is on the side arms
     */
    public abstract boolean isHvOnSide();

    /**
     * Lateral centre of each HV bushing, in block units along the model's authored (SOUTH-facing) X
     * axis. 0.0 and 1.0 are the block's own edges; values outside that range are arms overhanging it.
     * <p>
     * One entry means a single bushing serving every HV wire -- the 1-wire tops model exactly one, in
     * the centre. Two entries are a pair, index 0 being HV slot 1.
     *
     * @return the bushing centres, length 1 or 2
     */
    protected abstract double[] getHvBushingOffsets();

    /**
     * Height of the HV attachment point, in blocks above the master (lower) block.
     *
     * @return the attachment height
     */
    protected abstract double getHvBushingHeight();

    /**
     * @return true if a single bushing serves every HV wire on this variant
     */
    protected final boolean hasSingleHvBushing()
    {
        return getHvBushingOffsets().length == 1;
    }

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
        EnumFacing loaded = EnumFacing.byIndex(nbt.getInteger("facing"));
        facing = loaded.getAxis() != EnumFacing.Axis.Y ? loaded : EnumFacing.NORTH;
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
            // Top-mounted HV, with the MV/LV relay also on top.
            // Where there is only one bushing it stands in the centre, so the centre band has to be
            // the HV point: reserving it for the relay, as the two-bushing layout does, left the
            // single bushing impossible to click and HV reachable only off the model. The relay is
            // invisible and accepts a click anywhere, so it gives up the centre here instead.
            if (hasSingleHvBushing())
            {
                boolean onCentre = (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
                        ? target.hitX > 0.35 && target.hitX < 0.65
                        : target.hitZ > 0.35 && target.hitZ < 0.65;
                return onCentre ? 1 : 0;
            }

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
                // The slots are occupancy, not identity: which bushing a wire draws to is worked out from the
                // wires themselves (isHvSlot1), so either side takes a wire while there is room for one.
                return isTwoWire() ? hvCable2 == null : hvCable1 == null;
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

        // IE registers the connection before telling either end, so the live wires already include it.
        syncCablesFromConnections();
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
        if (connection == null)
        {
            // IE's "clearing everything" call, made before the connections are gone.
            hvCable1 = null;
            hvCable2 = null;
            mvLvCableCount = 0;
            mvLvLimitType = null;
        }
        else
        {
            // The wire has already left IE's tables. Counting down instead cleared both HV slots whenever IE
            // reported one removal twice, and could never recover from a pasted or edited transformer.
            syncCablesFromConnections();
        }

        this.markDirty();
        if (world != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 2);
        }
    }

    @Override
    public void validate()
    {
        super.validate();
        if (world != null && !world.isRemote)
            ApiUtils.addFutureServerTask(world, () -> {
                if (!isInvalid() && syncCablesFromConnections())
                    markDirty();
            });
    }

    /**
     * Rebuilds the slot tracking from the wires IE actually holds for this transformer. A pasted transformer
     * otherwise keeps the slots of the one it was copied from and refuses every HV wire, and wires cleaned up after a
     * world edit never reach it.
     *
     * @return whether anything changed
     */
    private boolean syncCablesFromConnections()
    {
        if (world == null || world.isRemote || dummy != 0)
            return false;
        int steel = 0, mvLv = 0;
        WireType mvLvType = null;
        Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(world, getPos());
        if (conns != null)
            for (Connection c : conns)
            {
                if (c.cableType == WireType.STEEL)
                    steel++;
                else
                {
                    mvLv++;
                    if (mvLvType == null)
                        mvLvType = c.cableType;
                }
            }
        WireType h1 = steel >= 1 ? WireType.STEEL : null;
        WireType h2 = steel >= 2 ? WireType.STEEL : null;
        if (h1 == hvCable1 && h2 == hvCable2 && mvLv == mvLvCableCount && mvLvType == mvLvLimitType)
            return false;
        hvCable1 = h1;
        hvCable2 = h2;
        mvLvCableCount = mvLv;
        mvLvLimitType = mvLvType;
        return true;
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
     * Determines whether the given connection belongs to HV slot 1 by querying
     * IE's live connection list and sorting the two HV wires deterministically
     * by their remote endpoint position. The wire whose remote end has the
     * smaller packed-long position is assigned to slot 1.
     */
    private boolean isHvSlot1(Connection con)
    {
        if (world == null)
            return true;

        BlockPos myPos = getPos();
        BlockPos otherEnd = con.start.equals(myPos) ? con.end : con.start;
        Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(world, myPos);
        if (conns != null)
        {
            for (Connection c : conns)
            {
                if (c.cableType != WireType.STEEL)
                    continue;
                // Skip the same logical connection (same remote endpoint)
                BlockPos cOtherEnd = c.start.equals(myPos) ? c.end : c.start;
                if (cOtherEnd.equals(otherEnd))
                    continue;
                // Found a different HV connection -- the one whose remote
                // endpoint has the smaller packed-long goes to slot 1
                return otherEnd.toLong() < cOtherEnd.toLong();
            }
        }
        // Only one HV connection -- slot 1
        return true;
    }

    /**
     * Gets the 3D offset for an HV connection. Offsets are derived from the
     * block model bushing positions so wires attach at the visual connection
     * points rather than inside the transformer body.
     */
    protected Vec3d getHvConnectionOffset(Connection con)
    {
        double[] bushings = getHvBushingOffsets();
        // A lone bushing takes every HV wire; a pair splits them by slot.
        double lateral = bushings.length == 1 ? bushings[0] : (isHvSlot1(con) ? bushings[0] : bushings[1]);
        double y = getHvBushingHeight();

        // The models are authored facing SOUTH, and the blockstate turns them from there.
        if (facing == EnumFacing.NORTH)
            return new Vec3d(lateral, y, 0.5);
        else if (facing == EnumFacing.SOUTH)
            return new Vec3d(1.0 - lateral, y, 0.5);
        else if (facing == EnumFacing.WEST)
            return new Vec3d(0.5, y, 1.0 - lateral);
        else // EAST
            return new Vec3d(0.5, y, lateral);
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

    // === ICacheData ===

    @Override
    public Object[] getCacheData()
    {
        return new Object[]{ getClass().getName() };
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