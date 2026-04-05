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
import net.minecraft.util.math.Vec3d;

/**
 * RealGrid Cutoffs
 *
 * Modelled after Immersive Engineering's MV/LV Breaker Switch, extended to support all
 * three voltage tiers (HV, MV, LV) simultaneously on up to {@value MAX_WIRES} wire connections.
 *
 * Unlike the standard IE Breaker Switch, which locks itself to the voltage of the first
 * connected wire, this tile entity deliberately omits the limitType lock so that HV, MV,
 * and LV wires can coexist on the same device.
 *
 * Behaviour summary (mirrors IE Breaker Switch):
 *  - Right-click  (empty hand / non-hammer) : manually toggle switch open / closed
 *  - Sneak + Engineer's Hammer              : toggle redstone-control inversion
 *  - Redstone signal present (normal mode)  : opens switch  (power stops)
 *  - Redstone signal present (inverted mode): closes switch (power flows)
 *  - Outputs a weak / strong redstone signal indicating its current state
 */
public class TileEntityCutoffSwitch4 extends TileEntityImmersiveConnectable
        implements IDirectionalTile, IBlockBounds, IRedstoneOutput, ITickable, IHammerInteraction {

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    /** Maximum number of wire connections accepted across all voltage tiers. */
    public static final int MAX_WIRES = 3;

    // -----------------------------------------------------------------------
    // State fields
    // -----------------------------------------------------------------------

    public EnumFacing facing = EnumFacing.NORTH;

    /** Live count of currently attached wires (all tiers combined). */
    public int wires = 0;

    /** true = switch closed (power may flow); false = switch open (power blocked). */
    public boolean active = true;

    /**
     * When false (normal): a redstone signal opens the switch.
     * When true (inverted): a redstone signal closes the switch.
     * Toggled via Sneak + Engineer's Hammer, matching IE Breaker Switch convention.
     */
    public boolean redstoneControlInverted = false;

    // -----------------------------------------------------------------------
    // Voltage capability — all three IE tiers accepted
    // -----------------------------------------------------------------------

    @Override protected boolean canTakeLV() { return true; }
    @Override protected boolean canTakeMV() { return true; }
    @Override protected boolean canTakeHV() { return true; }

    /** Not a passive relay; this device actively gates energy flow. */
    @Override protected boolean isRelay()   { return false; }

    @Override public boolean canConnect()       { return true; }
    @Override public boolean isEnergyOutput()   { return false; }

    @Override
    public int outputEnergy(int amount, boolean simulate, int energyType) { return 0; }

    /**
     * Core gate: the IE wire network calls this on every connection traversal.
     * Returning {@code active} is identical to the IE Breaker Switch's behaviour.
     */
    @Override
    public boolean allowEnergyToPass(Connection con) { return active; }

    // -----------------------------------------------------------------------
    // Manual toggle (called by BlockCutoffSwitch on right-click)
    // -----------------------------------------------------------------------

    /**
     * Flips the switch state immediately, bypassing redstone.
     * Equivalent to the manual click toggle on the IE Breaker Switch.
     * Must be called server-side only.
     */
    public void toggleSwitch() {
        active = !active;
        onSwitchStateChanged();
    }

    // -----------------------------------------------------------------------
    // IHammerInteraction — Sneak + Hammer inverts redstone control
    // (IE Breaker Switch uses the same pattern via IHammerInteraction on its TE)
    // -----------------------------------------------------------------------

    @Override
    public boolean hammerUseSide(EnumFacing side, EntityPlayer player,
                                 float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) return false;
        if (world.isRemote)      return true; // client-side: consume without acting

        redstoneControlInverted = !redstoneControlInverted;

        // Re-evaluate immediately so the switch doesn't wait a full tick to respond
        boolean redstonePowered = isRedstonePowered();
        active = redstoneControlInverted ? redstonePowered : !redstonePowered;

        onSwitchStateChanged();
        return true;
    }

    // -----------------------------------------------------------------------
    // ITickable — poll redstone every tick (mirrors IE Breaker Switch tick logic)
    // -----------------------------------------------------------------------

    @Override
    public void update() {
        if (world.isRemote) return;

        boolean redstonePowered = isRedstonePowered();
        // Normal : no signal → closed; signal → open
        // Inverted: signal → closed; no signal → open
        boolean shouldBeActive = redstoneControlInverted ? redstonePowered : !redstonePowered;

        if (shouldBeActive != active) {
            active = shouldBeActive;
            onSwitchStateChanged();
        }
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    /**
     * Shared post-state-change routine.  Resets the IE network cache when
     * multiple wires are present (required to propagate the gate change),
     * then notifies neighbours and the client renderer.
     */
    private void onSwitchStateChanged() {
        if (wires > 1) {
            ImmersiveNetHandler.INSTANCE.resetCachedIndirectConnections();
        }
        notifyNeighbours();
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            // Sends the active flag to the client via receiveClientEvent
            world.addBlockEvent(getPos(), getBlockType(), active ? 1 : 0, 0);
        }
    }

    /** Checks all six faces for a redstone signal. */
    private boolean isRedstonePowered() {
        for (EnumFacing side : EnumFacing.VALUES) {
            if (world.getRedstonePower(pos.offset(side), side) > 0) return true;
        }
        return false;
    }

    public void notifyNeighbours() {
        markDirty();
        world.notifyNeighborsOfStateChange(getPos(), getBlockType(), true);
        for (EnumFacing f : EnumFacing.VALUES) {
            world.notifyNeighborsOfStateChange(getPos().offset(f), getBlockType(), true);
        }
    }

    // -----------------------------------------------------------------------
    // Wire connection management — multi-voltage, no limitType lock
    // -----------------------------------------------------------------------

    /**
     * Accepts any IE energy wire (LV copper, MV electrum, HV steel) up to MAX_WIRES
     * total connections.  The key difference from the stock IE Breaker Switch is that
     * the {@code limitType} lock is intentionally absent here, enabling mixed-voltage
     * connections on the same device.
     */
    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target) {
        if (cableType == null || !cableType.isEnergyWire()) return false;
        return wires < MAX_WIRES;
        // Note: no limitType check — all three voltage tiers are accepted simultaneously.
    }

    @Override
    public void connectCable(WireType cableType, TargetingInfo target,
                             IImmersiveConnectable other) {
        // Do NOT set this.limitType — that would lock future connections to one voltage.
        wires++;
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    /**
     * Returns {@code null} to signal to the IE wire system that no single voltage
     * type is enforced, allowing HV, MV, and LV wires to coexist on this device.
     */
    @Override
    public WireType getCableLimiter(TargetingInfo target) {
        return null;
    }

    @Override
    public void removeCable(Connection connection) {
        WireType type = connection != null ? connection.cableType : null;

        if (type == null) {
            // Full reset (e.g., world reload edge case)
            wires = 0;
        } else {
            wires = Math.max(0, wires - 1);
        }

        // Clear the inherited limitType when the last wire is removed so a fresh
        // connection on re-attachment does not inherit a stale type from the parent class.
        if (wires <= 0) {
            wires = 0;
            this.limitType = null;
        }

        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    // -----------------------------------------------------------------------
    // NBT persistence
    // -----------------------------------------------------------------------

    @Override
    public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setInteger("facing", facing.ordinal());
        nbt.setInteger("wires", wires);
        nbt.setBoolean("active", active);
        nbt.setBoolean("redstoneControlInverted", redstoneControlInverted);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        facing  = EnumFacing.byIndex(nbt.getInteger("facing"));
        wires   = nbt.getInteger("wires");
        active  = nbt.getBoolean("active");

        // Backwards-compatibility: earlier builds used the key "inverted"
        if (nbt.hasKey("inverted")) {
            redstoneControlInverted = nbt.getBoolean("inverted");
        } else {
            redstoneControlInverted = nbt.getBoolean("redstoneControlInverted");
        }
    }

    // -----------------------------------------------------------------------
    // Rendering / connection geometry
    // -----------------------------------------------------------------------

    @Override
    public Vec3d getRaytraceOffset(IImmersiveConnectable link) {
        return new Vec3d(0.5, 0.5, 0.5);
    }

    @Override
    public Vec3d getConnectionOffset(Connection con) {
        return new Vec3d(0.5, 0.5, 0.5);
    }

    /**
     * Receives the active flag pushed by {@code world.addBlockEvent} so the
     * client renderer can update without a full chunk re-render.
     */
    @Override
    public boolean receiveClientEvent(int id, int arg) {
        if (super.receiveClientEvent(id, arg)) return true;
        this.active = (id == 1);
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
        return true;
    }

    // -----------------------------------------------------------------------
    // IDirectionalTile
    // -----------------------------------------------------------------------

    @Override public EnumFacing getFacing()               { return facing; }
    @Override public void      setFacing(EnumFacing f)    { this.facing = f; }
    @Override public int       getFacingLimitation()      { return 2; } // Horizontal only

    @Override
    public boolean mirrorFacingOnPlacement(EntityLivingBase placer) { return true; }

    @Override
    public boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ,
                                   EntityLivingBase entity) { return false; }

    @Override
    public boolean canRotate(EnumFacing axis) { return false; }

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

    /**
     * Weak output mirrors the switch state XOR the inversion flag, exactly as
     * the IE Breaker Switch does for its own redstone feedback.
     */
    @Override
    public int getWeakRSOutput(IBlockState state, EnumFacing side) {
        return (active ^ redstoneControlInverted) ? 15 : 0;
    }

    /** Strong output on the rear face only, consistent with IE convention. */
    @Override
    public int getStrongRSOutput(IBlockState state, EnumFacing side) {
        return (side.getOpposite() == facing && (active ^ redstoneControlInverted)) ? 15 : 0;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, EnumFacing side) { return true; }
}
