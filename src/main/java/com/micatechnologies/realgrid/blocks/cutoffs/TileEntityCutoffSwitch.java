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
import blusunrize.immersiveengineering.common.util.IESounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Cutoff Switch Tile Entity
 *
 * <p>Supports up to 2 wire connections accepting any mix of HV, MV, and LV,
 * mirroring the IE MV/LV Breaker Switch model across all three voltage tiers.
 */
public class TileEntityCutoffSwitch extends TileEntityImmersiveConnectable
        implements IDirectionalTile, IBlockBounds, IHammerInteraction, IRedstoneOutput, ITickable {

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    /** Maximum simultaneous wire connections. Accepts any mix of HV, MV, LV. */
    public static final int MAX_WIRES = 2;

    /**
     * Block event ID sent when the switch opens (active=false).
     * Must not collide with IE's reserved IDs (-1, 254, 255).
     */
    private static final int EVENT_OPEN  = 10;

    /**
     * Block event ID sent when the switch closes (active=true).
     * Must not collide with IE's reserved IDs (-1, 254, 255).
     */
    private static final int EVENT_CLOSE = 11;

    // -----------------------------------------------------------------------
    // Persistent state
    // -----------------------------------------------------------------------

    public EnumFacing facing = EnumFacing.NORTH;
    public int wires = 0;

    /** true = switch closed (power flows); false = switch open (power blocked). */
    public boolean active = true;

    /**
     * When false (default): redstone HIGH opens the switch (stops power).
     * When true (inverted):  redstone HIGH closes the switch (allows power).
     */
    public boolean inverted = false;

    /**
     * Tracks the redstone power state seen on the last evaluated tick.
     * Saved to NBT so manual overrides survive a world reload.
     * update() only changes switch state when isRedstonePowered() differs
     * from this value, so manual right-click toggles persist while redstone
     * is stable.
     */
    private boolean lastRedstoneState = false;

    // -----------------------------------------------------------------------
    // Reentrant guard (transient \u2014 never serialised)
    // -----------------------------------------------------------------------

    private transient boolean stateChanging = false;

    // -----------------------------------------------------------------------
    // IImmersiveConnectable \u2014 voltage capability
    // -----------------------------------------------------------------------

    @Override protected boolean canTakeLV() { return true; }
    @Override protected boolean canTakeMV() { return true; }
    @Override protected boolean canTakeHV() { return true; }
    @Override protected boolean isRelay()   { return false; }

    @Override public boolean canConnect()      { return true;  }
    @Override public boolean isEnergyOutput()  { return false; }

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
        return true; // accepts HV, MV, LV simultaneously
    }

    @Override
    public void connectCable(WireType cableType, TargetingInfo target,
                             IImmersiveConnectable other) {
        // limitType intentionally NOT set \u2014 all three voltage tiers can coexist.
        wires++;
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 2);
        }
    }

    @Override
    public WireType getCableLimiter(TargetingInfo target) {
        return null; // no per-voltage lock
    }

    /**
     * Called by IE's wire system when a wire is removed \u2014 either by the
     * Engineer's Wire Cutters or by block destruction.
     *
     * <p>IE's flow: {@code ImmersiveNetHandler.removeConnection()} (or
     * {@code clearAllConnectionsFor()}) removes the entry from its internal
     * multimap <em>first</em>, then calls {@code removeCable()} on both
     * endpoints as a notification. This method must <strong>not</strong> call
     * back into the handler; doing so would recurse infinitely.
     *
     * <p>Calling {@code super.removeCable()} handles:
     * <ol>
     *   <li>Checking remaining connections and clearing {@code limitType} when
     *       none remain.</li>
     *   <li>{@code markDirty()} so the TE is saved.</li>
     *   <li>{@code world.notifyBlockUpdate()} so clients re-render.</li>
     * </ol>
     * We then update our local {@code wires} counter on top of that.
     */
    @Override
    public void removeCable(Connection connection) {
        // Delegate to IE's base implementation first (limitType, markDirty,
        // notifyBlockUpdate).
        super.removeCable(connection);

        // Update our local counter.
        // connection == null is the "bulk clear" signal emitted by
        // clearAllConnectionsFor(); treat it as a full reset.
        if (connection == null) {
            wires = 0;
        } else {
            wires = Math.max(0, wires - 1);
        }

        // Belt-and-suspenders: ensure clean state when no wires remain.
        if (wires <= 0) {
            wires     = 0;
            limitType = null;
        }

        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 2);
        }
    }

    // -----------------------------------------------------------------------
    // Block destruction \u2014 clean up all IE wire connections
    // -----------------------------------------------------------------------

    /**
     * Called by {@code BlockCutoffSwitchBase.breakBlock()} <em>before</em>
     * {@code super.breakBlock()} so this TileEntity is still accessible at
     * {@code pos}.
     *
     * <p><b>Fix:</b> The previous code called
     * {@code ImmersiveNetHandler.INSTANCE.removeAllWires(world, pos, true)},
     * which does <em>not exist</em> in the IE API and caused a compile error.
     * The correct method is
     * {@link ImmersiveNetHandler#clearAllConnectionsFor(BlockPos, World, boolean)}.
     *
     * <p>{@code clearAllConnectionsFor(pos, world, true)} performs the
     * following in a single call:
     * <ol>
     *   <li>Clears this node's own connection set from the handler's
     *       multimap.</li>
     *   <li>Calls {@code iic.removeCable(null)} on this block (the bulk-reset
     *       signal \u2014 triggers our override above, setting
     *       {@code wires = 0}).</li>
     *   <li>Scans every other node's connection set and removes any entry
     *       that references this position.</li>
     *   <li>Calls {@code removeCable(con)} on both this block and the remote
     *       endpoint for each removed entry, so connected blocks receive their
     *       own removal callbacks and clean up their local state.</li>
     *   <li>Spawns the wire-coil item drop at the remote end (when
     *       {@code doDrops=true} and the {@code doTileDrops} game-rule is
     *       enabled).</li>
     *   <li>Fires IE's block event (id=\u221219) on every connected position so
     *       clients stop rendering orphaned wire segments immediately.</li>
     * </ol>
     *
     * <p>The explicit {@code wires = 0; limitType = null;} resets below are
     * redundant (step 2 above already handles this via our
     * {@link #removeCable(Connection)} override) but are kept as a defensive
     * fallback.
     */
    public void onBlockDestroyed() {
        if (world == null || world.isRemote) return;

        // Tear down every wire attached to this position:
        //   - notifies all remote endpoints via their removeCable() callbacks
        //   - drops wire coils (doDrops = true)
        //   - fires client render events so orphaned wire segments disappear
        ImmersiveNetHandler.INSTANCE.clearAllConnectionsFor(pos, world, true);

        // Defensive reset \u2014 clearAllConnectionsFor already triggers
        // removeCable(null) which sets wires = 0, but guard here as well.
        wires     = 0;
        limitType = null;
    }

    // -----------------------------------------------------------------------
    // ITickable \u2014 redstone polling
    // -----------------------------------------------------------------------

    /**
     * Only acts when the redstone signal level actually changes since the
     * previous evaluation. While redstone is stable this is a no-op, and any
     * manually toggled state persists.
     */
    @Override
    public void update() {
        if (world == null || world.isRemote || stateChanging) return;

        boolean redstonePowered = isRedstonePowered();

        // No change since last check \u2014 preserve manually set state.
        if (redstonePowered == lastRedstoneState) return;
        lastRedstoneState = redstonePowered;

        // Redstone changed \u2014 drive the switch state.
        // Normal:   HIGH = open  (blocks power)
        // Inverted: HIGH = close (allows power)
        boolean shouldBeActive = inverted ? redstonePowered : !redstonePowered;
        if (shouldBeActive != active) {
            active = shouldBeActive;
            applyStateChange();
        }
    }

    // -----------------------------------------------------------------------
    // State change \u2014 single guarded notification pass
    // -----------------------------------------------------------------------

    /**
     * Applies a switch-state change safely.
     *
     * <p>Notification sequence (one pass, no recursion):
     * <ol>
     *   <li>IE network cache reset (only when more than one wire is
     *       attached).</li>
     *   <li>{@code world.setBlockState} flag 6 \u2014 writes the updated
     *       {@code ACTIVE} property and sends it to clients so the correct
     *       open/closed model is rendered immediately.</li>
     *   <li>{@code world.playSound} \u2014 broadcasts the lever-click audio.</li>
     *   <li>{@code world.notifyNeighborsOfStateChange} \u2014 one pass for
     *       redstone propagation.</li>
     *   <li>{@code markDirty()} \u2014 persist new state to disk.</li>
     *   <li>{@code world.notifyBlockUpdate} flag 2 \u2014 queues
     *       {@code SPacketUpdateTileEntity} to clients.</li>
     *   <li>{@code world.addBlockEvent} with safe IDs \u2014 secondary client
     *       render guarantee.</li>
     * </ol>
     */
    public void applyStateChange() {
        if (stateChanging) return;
        stateChanging = true;
        try {
            if (wires > 1) {
                ImmersiveNetHandler.INSTANCE.resetCachedIndirectConnections();
            }
            if (world != null && !world.isRemote) {
                // Write the updated ACTIVE property into the block state.
                // Flag 6 = flag 2 (send to clients) | flag 4 (suppress observers).
                IBlockState newBlockState = world.getBlockState(pos)
                        .withProperty(BlockCutoffSwitchBase.ACTIVE, active);
                world.setBlockState(pos, newBlockState, 6);

                // Play the IE switch sound (broadcast to nearby players).
                world.playSound(null, getPos(), IESounds.direSwitch,
                        SoundCategory.BLOCKS, 2.5F, active ? 1.0F : 0.85F);

                // One neighbour-notification pass for redstone propagation.
                world.notifyNeighborsOfStateChange(getPos(), getBlockType(), true);

                markDirty();

                // Explicitly queue SPacketUpdateTileEntity to clients.
                IBlockState syncState = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, syncState, syncState, 2);

                // Secondary render guarantee using non-IE-reserved event IDs.
                world.addBlockEvent(getPos(), getBlockType(),
                        active ? EVENT_OPEN : EVENT_CLOSE, 0);
            }
        } finally {
            stateChanging = false;
        }
    }

    // -----------------------------------------------------------------------
    // IHammerInteraction
    // -----------------------------------------------------------------------

    @Override
    public boolean hammerUseSide(EnumFacing side, EntityPlayer player,
                                 float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        inverted = !inverted;
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
     * Handles block events fired by {@link #applyStateChange()}.
     *
     * <p>Our event IDs ({@code EVENT_OPEN=10}, {@code EVENT_CLOSE=11}) are
     * checked <em>before</em> delegating to {@code super} so IE's reserved
     * IDs (-1, 254, 255) are never intercepted. The only actions taken here
     * are updating {@code active} and scheduling a client render update \u2014
     * no neighbour notifications or network callbacks.
     */
    @Override
    public boolean receiveClientEvent(int id, int arg) {
        if (id == EVENT_OPEN || id == EVENT_CLOSE) {
            this.active = (id == EVENT_OPEN);
            if (world != null) {
                world.markBlockRangeForRenderUpdate(pos, pos);
            }
            return true;
        }
        return super.receiveClientEvent(id, arg);
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
    public Vec3d getConnectionOffset(Connection con) { return new Vec3d(0.5, 0.5, 0.5); }

    // -----------------------------------------------------------------------
    // IDirectionalTile
    // -----------------------------------------------------------------------

    @Override public EnumFacing getFacing()  { return facing; }
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
