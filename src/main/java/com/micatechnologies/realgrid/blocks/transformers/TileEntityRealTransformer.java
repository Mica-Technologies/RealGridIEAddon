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

    /** Class A arm bushings: midpoint of the insulator fin pair on each overhanging arm. */
    protected static final double CLASS_A_ARM_LEFT = -0.21875;  // model X = -3.5px
    protected static final double CLASS_A_ARM_RIGHT = 1.21875;  // model X = 19.5px
    /** Top of the Class A arm bushings, model Y = 29px: the wire rests on the bushing, not through it. */
    protected static final double CLASS_A_ARM_HEIGHT = 1.8125;
    /** Class A arms are centred on model Z = 9px, one pixel behind the block centre. */
    protected static final double CLASS_A_ARM_DEPTH = 0.5625;
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
     * the centre. Two entries are a pair; which wire goes to which is decided by where the wires come
     * from, see {@link #getHvConnectionOffset(Connection)}.
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
     * Depth of the HV attachment point along the model's authored Z axis. The top-mounted bushings sit
     * on the block's centre line; a variant whose bushings are modelled off it overrides this.
     *
     * @return the attachment depth, 0.5 by default
     */
    protected double getHvBushingDepth()
    {
        return 0.5;
    }

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
                // wires themselves (getHvConnectionOffset), so either side takes a wire while there is room for one.
                return hasHvCapacity();
            case 2:
                // Second HV connection - STEEL only (2-wire only)
                if (!isTwoWire())
                    return false;
                if (cableType != WireType.STEEL)
                    return false;
                return hasHvCapacity();
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

        // Establish connector state immediately. On some IE paths the handler's connection table is not observable
        // until after this callback returns; relying on the subsequent sync alone left a window in which a copper
        // relay could accept electrum, or a one-wire transformer could accept a second HV wire.
        int targetedConnector = getTargetedConnector(target);
        if (targetedConnector == 0)
        {
            if (mvLvLimitType == null)
                mvLvLimitType = cableType;
            mvLvCableCount++;
        }
        else if (targetedConnector == 1 || targetedConnector == 2)
        {
            if (hvCable1 == null)
                hvCable1 = cableType;
            else if (isTwoWire() && hvCable2 == null)
                hvCable2 = cableType;
        }

        // Do not sync here. This IE version can call us before inserting the connection in its table; a sync at
        // that instant erases the reservation above and makes a just-connected one-wire transformer accept a
        // second HV coil. validate() reconciles these fields with the authoritative table once registration ends.
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

    /**
     * Whether another HV wire may be attached. Stored state protects the client-side click path, while the live
     * handler count protects the server if a saved tile has not yet received its description packet. Taking the
     * larger count is deliberately conservative: a stale value may reject one click until validation, but can
     * never create an illegal extra HV connection.
     */
    private boolean hasHvCapacity()
    {
        int occupied = (hvCable1 == null ? 0 : 1) + (hvCable2 == null ? 0 : 1);
        if (world != null)
        {
            Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(world, getPos());
            if (conns != null)
            {
                int live = 0;
                for (Connection c : conns)
                    if (c.cableType == WireType.STEEL)
                        live++;
                occupied = Math.max(occupied, live);
            }
        }
        return occupied < (isTwoWire() ? 2 : 1);
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
            return getHvConnectionOffset(con);
        // MV/LV connection - invisible relay on top centre
        return new Vec3d(0.5, 2.0, 0.5);
    }

    /**
     * Where an HV wire meets this transformer, relative to the master block.
     *
     * <p>A lone bushing takes every wire. With a pair, a wire goes to the bushing nearer its far end, and when
     * two wires are attached they are shared out so that neither crosses the body to reach its bushing. The rule
     * this replaces sorted the wires by the packed position of their far end and knew nothing of where the
     * bushings were, so a wire arriving from the east was routinely sent to the west arm: it ran straight through
     * the tank and looked, from outside, as though it ended at the top centre of the body.
     */
    protected Vec3d getHvConnectionOffset(Connection con)
    {
        double[] bushings = getHvBushingOffsets();
        double y = getHvBushingHeight();
        double depth = getHvBushingDepth();
        if (bushings.length == 1)
            return modelToWorld(bushings[0], y, depth);

        Vec3d first = modelToWorld(bushings[0], y, depth);
        Vec3d second = modelToWorld(bushings[1], y, depth);
        BlockPos remote = remoteEndOf(con);
        Vec3d here = centreOf(remote);
        BlockPos otherRemote = otherHvRemoteEnd(remote);
        if (otherRemote == null)
            return here.distanceTo(first) <= here.distanceTo(second) ? first : second;

        Vec3d other = centreOf(otherRemote);
        double straight = here.distanceTo(first) + other.distanceTo(second);
        double crossed = here.distanceTo(second) + other.distanceTo(first);
        if (straight != crossed)
            return straight < crossed ? first : second;
        // Both far ends are equidistant from both bushings (stacked directly above, say). Any split is as good
        // as any other, so take a stable one that the other wire's call will agree with.
        return remote.toLong() < otherRemote.toLong() ? first : second;
    }

    /**
     * The far end of a connection attached to this transformer.
     */
    private BlockPos remoteEndOf(Connection con)
    {
        return con.start.equals(getPos()) ? con.end : con.start;
    }

    /**
     * The centre of a block, relative to the master block, in the same frame as the bushing offsets.
     */
    private Vec3d centreOf(BlockPos target)
    {
        return new Vec3d(target.getX() - getPos().getX() + 0.5,
                target.getY() - getPos().getY() + 0.5,
                target.getZ() - getPos().getZ() + 0.5);
    }

    /**
     * The far end of the other HV wire on this transformer, if there is one.
     *
     * @param remote the far end of the wire being placed, which is skipped
     */
    private BlockPos otherHvRemoteEnd(BlockPos remote)
    {
        if (world == null)
            return null;
        Set<Connection> conns = ImmersiveNetHandler.INSTANCE.getConnections(world, getPos());
        if (conns == null)
            return null;
        for (Connection c : conns)
        {
            if (c.cableType != WireType.STEEL)
                continue;
            BlockPos end = remoteEndOf(c);
            if (!end.equals(remote))
                return end;
        }
        return null;
    }

    /**
     * Turns a point in the model's own frame into an offset from the master block in the world. The models are
     * authored facing SOUTH, the blockstates leave south unrotated and turn the model from there, and this applies
     * the same turn -- so a point read off the model lands on the model however the block is placed. The previous
     * arithmetic mirrored the model left-to-right for every facing, which went unnoticed only because every
     * bushing pair is symmetric about the centre line.
     */
    protected Vec3d modelToWorld(double x, double y, double z)
    {
        switch (facing)
        {
            case NORTH:
                return new Vec3d(1.0 - x, y, 1.0 - z);
            case WEST:
                return new Vec3d(1.0 - z, y, x);
            case EAST:
                return new Vec3d(z, y, 1.0 - x);
            case SOUTH:
            default:
                return new Vec3d(x, y, z);
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
