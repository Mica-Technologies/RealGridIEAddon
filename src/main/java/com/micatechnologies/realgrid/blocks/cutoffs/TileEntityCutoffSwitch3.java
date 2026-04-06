package com.micatechnologies.realgrid.blocks.cutoffs;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHammerInteraction;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IRedstoneOutput;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Cutoff Switch Tile Entity
 *
 * Supports up to 3 wire connections accepting any mix of HV, MV, and LV,
 * mirroring the IE MV/LV Breaker Switch model across all three voltage tiers.
 *
 * State-change design
 * -------------------
 * Three bugs were causing the right-click toggle to silently revert and the
 * block model to stay frozen:
 *
 * Bug 1 - update() overrode the manual toggle every tick.
 *   update() evaluated shouldBeActive = !redstonePowered on every single tick.
 *   A right-click set active = !active, but within one tick update() set it
 *   straight back. Fix: track lastRedstoneState (saved to NBT) and only act
 *   when the redstone level changes. When redstone is stable, update() is a
 *   no-op, so the manually set state persists indefinitely.
 *
 * Bug 2 - applyStateChange() never updated the stored block state.
 *   world.notifyBlockUpdate(pos, state, state, 2) passed the SAME stale
 *   IBlockState object as both oldState and newState. The ACTIVE property in
 *   that object still reflected the previous toggle. Clients received a packet
 *   saying "block state unchanged" and the model never switched. Fix: call
 *   world.setBlockState(pos, newState, 6) where newState carries the updated
 *   ACTIVE value. Flag 6 = flag 2 (send new state to clients) + flag 4
 *   (no observer cascade), so clients receive the correct ACTIVE property
 *   immediately and re-render the matching model variant.
 *
 * Bug 3 - getActualState raced against receiveClientEvent.
 *   When the chunk re-rendered before the block event was delivered, getActualState
 *   still read the old te.active value from the client TE and overrode the correct
 *   block state the client had just received. Fix: because setBlockState (Bug 2 fix)
 *   now keeps the stored block state in sync, the metadata-driven state is always
 *   correct. getActualState is retained as a safe fallback for chunk-load order
 *   differences but it can no longer produce a stale override.
 *
 * StackOverflow prevention (from previous pass)
 * ----------------------------------------------
 * applyStateChange() is guarded by a transient reentrant flag (stateChanging).
 * notifyNeighborsOfStateChange is called once for the switch's own position only
 * (6 neighbours). receiveClientEvent uses markBlockRangeForRenderUpdate, a pure
 * render hint that cannot trigger a neighbour notification cascade.
 */
public class TileEntityCutoffSwitch3 extends TileEntityImmersiveConnectable
        implements IDirectionalTile, IBlockBounds, IHammerInteraction, IRedstoneOutput, ITickable {

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    /** Maximum simultaneous wire connections. Accepts any mix of HV, MV, LV. */
    public static final int MAX_WIRES = 3;

    // -----------------------------------------------------------------------
    // Persistent state
    // -----------------------------------------------------------------------

    public EnumFacing facing  = EnumFacing.NORTH;
    public int        wires   = 0;

    /**
     * true  = switch closed (power flows through)
     * false = switch open   (power blocked)
     */
    public boolean active   = true;

    /**
     * When false (default): redstone HIGH opens the switch (stops power).
     * When true (inverted): redstone HIGH closes the switch (allows power).
     * Toggled by sneak + Engineer's Hammer.
     */
    public boolean inverted = false;

    /**
     * Tracks the redstone power state seen on the last update() tick in which
     * redstone was evaluated. Saved to NBT so manual overrides survive a
     * world reload.
     *
     * update() only changes the switch state when isRedstonePowered() differs
     * from this value. While redstone is stable (no change), update() is a
     * no-op, allowing right-click manual toggles to persist across ticks.
     */
    private boolean lastRedstoneState = false;

    // -----------------------------------------------------------------------
    // Reentrant guard (transient - never serialised)
    // -----------------------------------------------------------------------

    /**
     * Set for the duration of applyStateChange() to break any re-entrant call
     * that could otherwise produce a StackOverflowError during notification
     * cascade propagation.
     */
    private transient boolean stateChanging = false;

    // -----------------------------------------------------------------------
    // IImmersiveConnectable - voltage capability
    // -----------------------------------------------------------------------

    @Override protected boolean canTakeLV() { return true; }
    @Override protected boolean canTakeMV() { return true; }
    @Override protected boolean canTakeHV() { return true; }
    @Override protected boolean isRelay()   { return false; }

    @Override public boolean canConnect()     { return true; }
    @Override public boolean isEnergyOutput() { return false; }

    @Override
    public int outputEnergy(int amount, boolean simulate, int energyType) { return 0; }

    @Override
    public boolean allowEnergyToPass(Connection con) { return active; }

    // -----------------------------------------------------------------------
    // Wire connection management
    // -----------------------------------------------------------------------

    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target) {
        if (cableType == null || !cableType.isEnergyWire()) return false;
        if (wires >= MAX_WIRES) return false;
        return true; // accepts HV, MV, LV simultaneously — no limitType lock
    }

    @Override
    public void connectCable(WireType cableType, TargetingInfo target,
                              IImmersiveConnectable other) {
        // limitType intentionally NOT set — all three voltage tiers can coexist
        wires++;
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 2); // flag 2: clients only
        }
    }

    @Override
    public WireType getCableLimiter(TargetingInfo target) {
        return null; // no per-voltage lock
    }

    @Override
    public void removeCable(Connection connection) {
        WireType type = connection != null ? connection.cableType : null;
        if (type == null) {
            wires = 0;
        } else {
            wires--;
        }
        if (wires <= 0) {
            wires     = 0;
            limitType = null;
        }
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 2); // flag 2: clients only
        }
    }

    // -----------------------------------------------------------------------
    // ITickable - redstone polling
    // -----------------------------------------------------------------------

    /**
     * Evaluates the switch state only when the redstone signal level has
     * changed since the previous tick.
     *
     * FIX (Bug 1): The previous implementation re-evaluated shouldBeActive on
     * every single tick. A right-click set active = !active, but within one
     * tick update() computed shouldBeActive = !redstonePowered and set it back,
     * silently reverting the manual toggle. By comparing against lastRedstoneState
     * and returning early when nothing has changed, update() is a no-op while
     * redstone is stable. The manually toggled active value is never overridden
     * unless the redstone signal actually changes.
     */
    @Override
    public void update() {
        if (world.isRemote || stateChanging) return;

        boolean redstonePowered = isRedstonePowered();

        // No redstone change since last check - leave manually set state alone
        if (redstonePowered == lastRedstoneState) return;
        lastRedstoneState = redstonePowered;

        // Redstone changed - let it drive the switch state
        // Normal:   HIGH = open (blocks power)
        // Inverted: HIGH = closed (allows power)
        boolean shouldBeActive = inverted ? redstonePowered : !redstonePowered;
        if (shouldBeActive != active) {
            active = shouldBeActive;
            applyStateChange();
        }
    }

    // -----------------------------------------------------------------------
    // State change - single guarded notification pass
    // -----------------------------------------------------------------------

    /**
     * Applies a switch state change safely. This is the only point from which
     * neighbour notifications and IE network resets are issued.
     *
     * FIX (Bug 2): The previous implementation called
     *   world.notifyBlockUpdate(pos, state, state, 2)
     * passing the same IBlockState object for both oldState and newState. That
     * object still carried the previous ACTIVE value, so the client received
     * a "block state unchanged" packet and never switched the model.
     *
     * The fix uses world.setBlockState(pos, newState, 6) where newState has the
     * updated ACTIVE property. Flag 6 = flag 2 (send to clients) + flag 4 (no
     * observer cascade). Clients immediately receive the new block state, look up
     * the matching model variant from the ACTIVE property, and re-render.
     *
     * Notification sequence (one pass, no recursion):
     *   1. IE network indirect-connection cache reset (if more than one wire)
     *   2. world.setBlockState with flag 6 — updates stored block state AND sends
     *      it to clients with the correct ACTIVE property value
     *   3. world.notifyNeighborsOfStateChange — one call, 6 adjacent blocks, for
     *      redstone propagation only (no per-face offset loop)
     *   4. markDirty — persist new state to disk
     *   5. world.addBlockEvent — syncs the active flag to the client TE instance
     *      so getActualState() and allowEnergyToPass() read correctly on the client
     */
    public void applyStateChange() {
        if (stateChanging) return;
        stateChanging = true;
        try {
            if (wires > 1) {
                ImmersiveNetHandler.INSTANCE.resetCachedIndirectConnections();
            }

            if (world != null && !world.isRemote) {
                // FIX (Bug 2): build a new IBlockState with the updated ACTIVE
                // value and set it in the world. Flag 6 = flag 2 (send to
                // clients) + flag 4 (suppress observer cascades). Clients
                // receive the correct ACTIVE property and re-render the matching
                // model variant immediately.
                IBlockState newBlockState = world.getBlockState(pos)
                        .withProperty(BlockCutoffSwitch3.ACTIVE, active);
                world.setBlockState(pos, newBlockState, 6);

                // Notify the 6 immediate neighbours once for redstone propagation.
                // No per-face offset loop — that was the StackOverflow source.
                world.notifyNeighborsOfStateChange(getPos(), getBlockType(), true);

                markDirty();

                // Sync the TE's active flag to the client TE instance.
                // receiveClientEvent will update te.active and call
                // markBlockRangeForRenderUpdate as a secondary render guarantee.
                world.addBlockEvent(getPos(), getBlockType(), active ? 1 : 0, 0);
            }
        } finally {
            stateChanging = false;
        }
    }

    // -----------------------------------------------------------------------
    // IHammerInteraction
    // -----------------------------------------------------------------------

    /**
     * Sneak + Engineer's Hammer toggles the redstone inversion flag and
     * immediately re-evaluates the switch state under the new flag.
     */
    @Override
    public boolean hammerUseSide(EnumFacing side, EntityPlayer player,
                                  float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        inverted = !inverted;
        // Recompute active from the current (stable) redstone level under
        // the new inversion flag so the switch responds without waiting for
        // a redstone change to arrive.
        boolean redstonePowered = isRedstonePowered();
        boolean shouldBeActive  = inverted ? redstonePowered : !redstonePowered;
        active = shouldBeActive;
        applyStateChange();
        return true;
    }

    // -----------------------------------------------------------------------
    // Client event receiver
    // -----------------------------------------------------------------------

    /**
     * Called on both server and client when a block event fired by
     * applyStateChange() is processed.
     *
     * FIX (Bug 3): This method must NOT call applyStateChange() or any variant
     * of world.notifyBlockUpdate() with flag 1. The only permitted action is to
     * update te.active and schedule a render update via markBlockRangeForRenderUpdate,
     * which is a pure render hint with no game-logic propagation.
     *
     * Because applyStateChange() now writes the correct block state via setBlockState
     * (Bug 2 fix), the model is already correct by the time this is called. The
     * markBlockRangeForRenderUpdate call here acts as a secondary guarantee that
     * covers the rare case where a chunk re-render slipped in before this event
     * was delivered.
     */
    @Override
    public boolean receiveClientEvent(int id, int arg) {
        if (super.receiveClientEvent(id, arg)) return true;
        this.active = (id == 1);
        // Pure render hint — no neighbour notifications, no network callbacks
        if (world != null) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
        return true;
    }

    // -----------------------------------------------------------------------
    // Redstone helpers
    // -----------------------------------------------------------------------

    public boolean isRedstonePowered() {
        for (EnumFacing side : EnumFacing.VALUES) {
            if (world.getRedstonePower(pos.offset(side), side) > 0) return true;
        }
        return false;
    }

    // -----------------------------------------------------------------------
    // NBT serialisation
    // -----------------------------------------------------------------------

    @Override
    public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setInteger("facing",            facing.ordinal());
        nbt.setInteger("wires",             wires);
        nbt.setBoolean("active",            active);
        nbt.setBoolean("inverted",          inverted);
        // Persist lastRedstoneState so manual overrides survive a world reload.
        // Without this, the first tick after reload would see a redstone-state
        // "change" from the default false and potentially override a manual toggle.
        nbt.setBoolean("lastRedstoneState", lastRedstoneState);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        facing            = EnumFacing.byIndex(nbt.getInteger("facing"));
        wires             = nbt.getInteger("wires");
        active            = nbt.getBoolean("active");
        inverted          = nbt.getBoolean("inverted");
        lastRedstoneState = nbt.getBoolean("lastRedstoneState");
    }

    // -----------------------------------------------------------------------
    // Wire geometry
    // -----------------------------------------------------------------------

    @Override
    public Vec3d getRaytraceOffset(IImmersiveConnectable link) { return new Vec3d(0.5, 0.5, 0.5); }

    @Override
    public Vec3d getConnectionOffset(Connection con)          { return new Vec3d(0.5, 0.5, 0.5); }

    // -----------------------------------------------------------------------
    // IDirectionalTile
    // -----------------------------------------------------------------------

    @Override public EnumFacing getFacing()    { return facing; }
    @Override public void setFacing(EnumFacing f) { this.facing = f; }
    @Override public int getFacingLimitation() { return 2; } // horizontal only
    @Override public boolean mirrorFacingOnPlacement(EntityLivingBase placer) { return true; }
    @Override public boolean canHammerRotate(EnumFacing side, float hx, float hy,
                                              float hz, EntityLivingBase entity) { return false; }
    @Override public boolean canRotate(EnumFacing axis) { return false; }

    // -----------------------------------------------------------------------
    // IBlockBounds
    // -----------------------------------------------------------------------

    @Override
    public float[] getBlockBounds() {
        return new float[]{ 0.1875f, 0.125f, 0.1875f, 0.8125f, 0.875f, 0.8125f };
    }

    // -----------------------------------------------------------------------
    // IRedstoneOutput
    // -----------------------------------------------------------------------

    @Override
    public int getWeakRSOutput(IBlockState state, EnumFacing side) {
        return (active ^ inverted) ? 15 : 0;
    }

    @Override
    public int getStrongRSOutput(IBlockState state, EnumFacing side) {
        return side.getOpposite() == facing && (active ^ inverted) ? 15 : 0;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, EnumFacing side) { return true; }
}
