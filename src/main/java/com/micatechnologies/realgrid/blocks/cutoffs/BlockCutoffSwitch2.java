package com.micatechnologies.realgrid.blocks.cutoffs;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.common.util.ChatUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import com.micatechnologies.realgrid.RealGrid;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;

/**
 * Cutoff Switch Block
 *
 * Modelled after Immersive Engineering's MV/LV Breaker Switch, extended to
 * support all three voltage tiers (HV, MV, LV) simultaneously via
 * TileEntityCutoffSwitch2.
 *
 * Interaction model
 * -----------------
 *   Right-click (no hammer)    — toggle switch open ↔ closed
 *   Sneak + Engineer's Hammer  — toggle redstone inversion mode
 *   Redstone signal             — drives switch state via TE tick
 *
 * Three-bug state-change fix (applied alongside TE changes)
 * ----------------------------------------------------------
 * Bug 1 — update() overrode manual right-click every tick (fixed in TE).
 *   update() now only fires on REDSTONE CHANGES via lastRedstoneState tracking.
 *   While redstone is stable, any manually toggled active value persists.
 *
 * Bug 2 — applyStateChange() passed the stale block state to clients (fixed in TE).
 *   applyStateChange() now calls world.setBlockState(pos, newState, 6) with a
 *   freshly built IBlockState carrying the updated ACTIVE property. Clients
 *   receive the correct ACTIVE value and immediately render the matching model.
 *
 * Bug 3 — getActualState raced against receiveClientEvent (fixed here + in TE).
 *   Because setBlockState (Bug 2 fix) keeps the stored block state in sync,
 *   the ACTIVE property in metadata is always authoritative. getActualState still
 *   reads from the TE as a safe fallback for chunk-load ordering differences, but
 *   it can no longer produce a stale override that freezes the model.
 */
public class BlockCutoffSwitch2 extends Block implements ITileEntityProvider {

    // -----------------------------------------------------------------------
    // Block state properties
    // -----------------------------------------------------------------------

    /**
     * Horizontal facing, set at placement time.
     * Stored in metadata bits 0-1 (values 0-3 via getHorizontalIndex).
     */
    public static final PropertyDirection FACING =
            PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    /**
     * Whether the switch is currently closed (true = power flows).
     * Stored in metadata bit 2. Kept in sync with TileEntityCutoffSwitch2#active
     * via applyStateChange() → world.setBlockState(pos, newState, 6) so that
     * clients always receive the correct model variant without a TE read race.
     */
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    /** Slightly-inset AABB matching the physical switch model. */
    private static final AxisAlignedBB SWITCH_AABB =
            new AxisAlignedBB(0.1875, 0.125, 0.1875, 0.8125, 0.875, 0.8125);

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public BlockCutoffSwitch2() {
        super(Material.IRON);
        setRegistryName(RealGrid.MODID, "maclean_cutoff_switch_closed_2");

        /*
         * setTranslationKey is the correct method name for Forge 1.12.2 builds
         * using MCP snapshot_20171003 or later. Older builds (MCP stable_39)
         * call this setUnlocalizedName instead; both map to func_149663_c.
         */
        setTranslationKey(RealGrid.MODID + ".maclean_cutoff_switch_closed_2");

        setHardness(3.0f);
        setResistance(15.0f);
        setDefaultState(blockState.getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(ACTIVE, true));
    }

    // -----------------------------------------------------------------------
    // Internal helper
    // -----------------------------------------------------------------------

    /** Casts the TE at pos to TileEntityCutoffSwitch2, or returns null. */
    @Nullable
    private TileEntityCutoffSwitch2 getSwitchTE(IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityCutoffSwitch2 ? (TileEntityCutoffSwitch2) te : null;
    }

    // -----------------------------------------------------------------------
    // Block state
    // -----------------------------------------------------------------------

    /**
     * Registers IEProperties.CONNECTIONS as an unlisted property so the IE wire
     * renderer can attach cables without consuming metadata bits.
     *
     * @SuppressWarnings("rawtypes") prevents raw IProperty[] and IUnlistedProperty[]
     * from being promoted to errors in -Werror Gradle builds. Generic array creation
     * (new IProperty<?>[]{}) is illegal in Java due to type erasure.
     */
    @Override
    @SuppressWarnings("rawtypes")
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this,
                new IProperty[]{ FACING, ACTIVE },
                new IUnlistedProperty[]{ IEProperties.CONNECTIONS });
    }

    /** Injects live IE connection geometry into the state for the wire renderer. */
    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityImmersiveConnectable) {
                state = ((IExtendedBlockState) state).withProperty(
                        IEProperties.CONNECTIONS,
                        ((TileEntityImmersiveConnectable) te).genConnBlockstate());
            }
        }
        return state;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    /**
     * Metadata layout:
     *   bits 0-1  — horizontal facing index (EnumFacing.getHorizontalIndex, 0-3)
     *   bit  2    — ACTIVE flag (1 = closed/power-flows, 0 = open/power-blocked)
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
                .withProperty(ACTIVE, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex()
                | (state.getValue(ACTIVE) ? 4 : 0);
    }

    /**
     * FIX (Bug 3): getActualState reads te.active as a fallback for chunk-load
     * ordering. Because applyStateChange() now keeps the stored block state in sync
     * via setBlockState(pos, newState, 6), this read is always consistent and never
     * races against a block event packet that hasn't arrived yet.
     *
     * Before the Bug 2 fix, the stored ACTIVE value was stale. A chunk re-render
     * triggered by the (stale) setBlockState packet would call getActualState, which
     * read the equally stale te.active, and both agreed on the wrong model variant.
     */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityCutoffSwitch2 te = getSwitchTE(world, pos);
        return te != null ? state.withProperty(ACTIVE, te.active) : state;
    }

    // -----------------------------------------------------------------------
    // Placement
    // -----------------------------------------------------------------------

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                             float hitX, float hitY, float hitZ,
                                             int meta, EntityLivingBase placer,
                                             EnumHand hand) {
        return getDefaultState()
                .withProperty(FACING, placer.getHorizontalFacing().getOpposite())
                .withProperty(ACTIVE, true);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                 EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote) {
            TileEntityCutoffSwitch2 te = getSwitchTE(world, pos);
            if (te != null) te.facing = state.getValue(FACING);
        }
    }

    // -----------------------------------------------------------------------
    // Player interaction
    // -----------------------------------------------------------------------

    /**
     * Right-click interaction — IE Breaker Switch priority order:
     *
     * 1. Sneak + Engineer's Hammer
     *    Delegates to TileEntityCutoffSwitch2#hammerUseSide which toggles the
     *    redstone inversion flag, immediately recomputes the correct active state
     *    from the current redstone level under the new flag, and calls
     *    applyStateChange() to propagate the change.
     *
     * 2. Right-click without a hammer (empty hand or non-hammer item)
     *    FIX (Bug 1 complement): Flips te.active directly, then calls
     *    applyStateChange(). This works because update() now only overrides
     *    active when redstone CHANGES (lastRedstoneState tracking). While
     *    redstone is stable, the manually flipped value persists indefinitely.
     *    applyStateChange() then writes the new ACTIVE property to the stored
     *    block state (Bug 2 fix) so the model updates immediately on clients.
     *
     * Server-side only: returns true on the client immediately so the arm swings
     * and default item behaviour (e.g. placing a block) is suppressed.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                     EntityPlayer player, EnumHand hand,
                                     EnumFacing facing,
                                     float hitX, float hitY, float hitZ) {
        // Client side: consume the interaction to suppress item use / arm swing.
        if (world.isRemote) return true;

        TileEntityCutoffSwitch2 te = getSwitchTE(world, pos);
        if (te == null) return false;

        ItemStack heldItem = player.getHeldItem(hand);

        // Priority 1: Sneak + Hammer — toggle redstone inversion
        if (Utils.isHammer(heldItem) && player.isSneaking()) {
            // hammerUseSide handles the inverted toggle, active recompute,
            // and applyStateChange() call.
            boolean handled = te.hammerUseSide(facing, player, hitX, hitY, hitZ);
            if (handled) {
                ChatUtils.sendServerNoSpamMessages(player, new TextComponentTranslation(
                        RealGrid.MODID + ".info.switch_inverted."
                        + (te.inverted ? "on" : "off")));
            }
            return handled;
        }

        // Priority 2: Right-click without hammer — manual open / close toggle
        if (!Utils.isHammer(heldItem)) {
            te.active = !te.active;
            // applyStateChange() writes the new ACTIVE property to the stored
            // block state (flag 6 = send to clients + no observer cascade) and
            // fires a block event so the client TE updates te.active for
            // getActualState(). The model switches on the client immediately.
            te.applyStateChange();
            ChatUtils.sendServerNoSpamMessages(player, new TextComponentTranslation(
                    RealGrid.MODID + ".info.cutoff_status."
                    + (te.active ? "closed" : "open")));
            return true;
        }

        return false;
    }

    // -----------------------------------------------------------------------
    // Redstone neighbour updates
    // -----------------------------------------------------------------------

    /**
     * TileEntityCutoffSwitch2#update() polls isRedstonePowered() every tick and
     * acts only when the result changes (lastRedstoneState tracking). No explicit
     * scheduling is required here.
     */
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos,
                                 Block blockIn, BlockPos fromPos) {
        // Intentionally empty — handled by TileEntityCutoffSwitch2.update()
    }

    // -----------------------------------------------------------------------
    // Redstone output
    // -----------------------------------------------------------------------

    @Override
    public boolean canProvidePower(IBlockState state) { return true; }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world,
                             BlockPos pos, EnumFacing side) {
        TileEntityCutoffSwitch2 te = getSwitchTE(world, pos);
        return te != null ? te.getWeakRSOutput(state, side) : 0;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world,
                               BlockPos pos, EnumFacing side) {
        TileEntityCutoffSwitch2 te = getSwitchTE(world, pos);
        return te != null ? te.getStrongRSOutput(state, side) : 0;
    }

    // -----------------------------------------------------------------------
    // Geometry and rendering
    // -----------------------------------------------------------------------

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SWITCH_AABB;
    }

    @Override public boolean isOpaqueCube(IBlockState state)           { return false; }
    @Override public boolean isFullCube(IBlockState state)             { return false; }
    @Override public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    // -----------------------------------------------------------------------
    // Client sync event
    // -----------------------------------------------------------------------

    /**
     * Forwards the block event queued by applyStateChange() to the TE.
     * TileEntityCutoffSwitch2#receiveClientEvent updates te.active and calls
     * markBlockRangeForRenderUpdate as a secondary render guarantee — the
     * primary model update arrives earlier via the setBlockState packet.
     */
    @Override
    public boolean eventReceived(IBlockState state, World world,
                                  BlockPos pos, int id, int param) {
        TileEntity te = world.getTileEntity(pos);
        return te != null && te.receiveClientEvent(id, param);
    }

    // -----------------------------------------------------------------------
    // TileEntity factory
    // -----------------------------------------------------------------------

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCutoffSwitch2();
    }
}
