package com.micatechnologies.realgrid.blocks.cutoffs;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHammerInteraction;
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
 * Modelled after Immersive Engineering's MV/LV Breaker Switch.  Supports all three
 * voltage tiers (HV, MV, LV) simultaneously through its TileEntity.
 *
 * Interaction model (matches IE Breaker Switch UX):
 *   Right-click (empty hand or non-hammer item)
 *       → manually toggle the switch open / closed (chat feedback provided)
 *   Sneak + Engineer's Hammer
 *       → invert redstone control logic via {@link IHammerInteraction} on the TE
 *   Redstone signal
 *       → drives the switch state; polled each tick by the TileEntity
 *
 * Wire cleanup on block removal:
 *   {@link #breakBlock} explicitly tears down all attached IE wire connections so
 *   dangling wire segments are not left in the world, consistent with IE's own
 *   block-removal behaviour.
 */
public class BlockCutoffSwitch3 extends Block implements ITileEntityProvider {

    // -----------------------------------------------------------------------
    // Block state properties
    // -----------------------------------------------------------------------

    /** Horizontal facing set on placement. */
    public static final PropertyDirection FACING =
            PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    /**
     * Mirrors the TileEntity's {@code active} flag for model / render selection.
     * Updated via {@link #getActualState} every frame on the client.
     */
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    /** Bounding box — slightly inset on all sides, matching the physical model. */
    private static final AxisAlignedBB SWITCH_AABB =
            new AxisAlignedBB(0.1875, 0.125, 0.1875, 0.8125, 0.875, 0.8125);

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public BlockCutoffSwitch3() {
        super(Material.IRON);
        setRegistryName(RealGrid.MODID, "maclean_cutoff_switch");
        setTranslationKey(RealGrid.MODID + ".maclean_cutoff_switch");
        setHardness(3.0f);
        setResistance(15.0f);
        setDefaultState(blockState.getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(ACTIVE, true));
    }

    // -----------------------------------------------------------------------
    // Block state
    // -----------------------------------------------------------------------

    /**
     * Extended block state includes {@link IEProperties#CONNECTIONS} so the IE
     * wire renderer can draw attached cables in the correct positions.
     */
    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this,
                new IProperty[]{ FACING, ACTIVE },
                new IUnlistedProperty[]{ IEProperties.CONNECTIONS });
    }

    /** Injects live IE connection data into the extended state for the wire renderer. */
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
        // TRANSLUCENT needed if the model uses any semi-transparent textures (e.g. glass lens)
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    // meta encodes 2 bits of facing (0-3) + 1 bit of active state (bit 2)
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byHorizontalIndex(meta & 3);
        boolean    active = (meta & 4) != 0;
        return getDefaultState().withProperty(FACING, facing).withProperty(ACTIVE, active);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facing = state.getValue(FACING).getHorizontalIndex();
        int active = state.getValue(ACTIVE) ? 4 : 0;
        return facing | active;
    }

    /** Pulls the live {@code active} flag from the TE for accurate model selection. */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCutoffSwitch3) {
            return state.withProperty(ACTIVE, ((TileEntityCutoffSwitch3) te).active);
        }
        return state;
    }

    // -----------------------------------------------------------------------
    // Placement
    // -----------------------------------------------------------------------

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                             float hitX, float hitY, float hitZ,
                                             int meta, EntityLivingBase placer, EnumHand hand) {
        // Face the switch toward the player who placed it, matching IE convention
        return getDefaultState()
                .withProperty(FACING, placer.getHorizontalFacing().getOpposite())
                .withProperty(ACTIVE, true);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                 EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityCutoffSwitch3) {
                ((TileEntityCutoffSwitch3) te).facing = state.getValue(FACING);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Interaction — IE Breaker Switch UX pattern
    // -----------------------------------------------------------------------

    /**
     * Handles all player interactions in a single method, following the IE Breaker
     * Switch dispatch order:
     *
     *  1. Sneak + Engineer's Hammer → delegate to {@link IHammerInteraction} on the TE
     *     (toggles redstone inversion, re-evaluates state immediately)
     *
     *  2. Right-click without a hammer → manually toggle the switch open/closed and
     *     send a localised status message, identical to clicking the IE Breaker Switch.
     *
     * Redstone-driven state changes are handled entirely in the TileEntity's
     * {@code update()} tick and do not require any block-level interaction.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                     EntityPlayer player, EnumHand hand,
                                     EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityCutoffSwitch3)) return false;

        TileEntityCutoffSwitch3 switchTe = (TileEntityCutoffSwitch3) te;
        ItemStack heldItem = player.getHeldItem(hand);

        // --- Path 1: Sneak + Hammer → invert redstone control (IHammerInteraction) ---
        if (Utils.isHammer(heldItem) && player.isSneaking()) {
            // hammerUseSide handles server/client guard internally
            boolean handled = switchTe.hammerUseSide(facing, player, hitX, hitY, hitZ);
            if (handled && !world.isRemote) {
                ChatUtils.sendServerNoSpamMessages(player,
                        new TextComponentTranslation(
                                RealGrid.MODID + ".info.switch_status." +
                                (switchTe.redstoneControlInverted ? "on" : "off")));
            }
            return handled;
        }

        // --- Path 2: Right-click (no hammer) → manual toggle, IE Breaker Switch style ---
        if (!Utils.isHammer(heldItem)) {
            if (!world.isRemote) {
                switchTe.toggleSwitch();
                ChatUtils.sendServerNoSpamMessages(player,
                        new TextComponentTranslation(
                                RealGrid.MODID + ".info.switch_status." +
                                (switchTe.active ? "closed" : "open")));
            }
            return true;
        }

        return false;
    }

    // -----------------------------------------------------------------------
    // Redstone neighbour changes
    // -----------------------------------------------------------------------

    /**
     * The TileEntity's {@code update()} tick already polls redstone every tick, so no
     * extra scheduling is required here.  The override is kept for clarity — future
     * optimisation could switch to a scheduled-tick approach (as in IE's own blocks)
     * where the TE only wakes on actual neighbour changes.
     */
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos,
                                 Block blockIn, BlockPos fromPos) {
        // Intentionally empty: state changes are driven by TileEntityCutoffSwitch3.update()
    }

    // -----------------------------------------------------------------------
    // Redstone output
    // -----------------------------------------------------------------------

    @Override
    public boolean canProvidePower(IBlockState state) { return true; }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world,
                             BlockPos pos, EnumFacing side) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCutoffSwitch3)
            return ((TileEntityCutoffSwitch3) te).getWeakRSOutput(state, side);
        return 0;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world,
                               BlockPos pos, EnumFacing side) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCutoffSwitch3)
            return ((TileEntityCutoffSwitch3) te).getStrongRSOutput(state, side);
        return 0;
    }

    // -----------------------------------------------------------------------
    // Wire cleanup on block removal
    // -----------------------------------------------------------------------

    /**
     * Explicitly removes all IE wire connections from this block before the
     * TileEntity is invalidated.  This mirrors the cleanup performed by IE's
     * own base block class and prevents dangling wire segments in the world.
     */
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityImmersiveConnectable) {
            ImmersiveNetHandler.INSTANCE.removeAllWires(
                    (TileEntityImmersiveConnectable) te, world);
        }
        super.breakBlock(world, pos, state);
    }

    // -----------------------------------------------------------------------
    // Geometry / rendering
    // -----------------------------------------------------------------------

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SWITCH_AABB;
    }

    @Override public boolean           isOpaqueCube(IBlockState state)  { return false; }
    @Override public boolean           isFullCube(IBlockState state)     { return false; }
    @Override public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos,
                                  int id, int param) {
        TileEntity te = world.getTileEntity(pos);
        return te != null && te.receiveClientEvent(id, param);
    }

    // -----------------------------------------------------------------------
    // TileEntity
    // -----------------------------------------------------------------------

    @Override public boolean hasTileEntity(IBlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCutoffSwitch3();
    }
}
