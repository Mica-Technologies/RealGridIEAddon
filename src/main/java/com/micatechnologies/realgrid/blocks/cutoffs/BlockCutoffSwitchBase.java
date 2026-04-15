package com.micatechnologies.realgrid.blocks.cutoffs;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.common.util.ChatUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.init.IRealGridTileEntityProvider;
import com.micatechnologies.realgrid.init.RealGridRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
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
 * Abstract base for all cutoff switch block variants.
 *
 * Modelled after Immersive Engineering's MV/LV Breaker Switch, extended to
 * support all three voltage tiers (HV, MV, LV) simultaneously via
 * {@link TileEntityCutoffSwitch}.
 *
 * Interaction model:
 *   Right-click (no hammer)      — toggle switch open / closed
 *   Sneak + Engineer's Hammer    — toggle redstone inversion mode
 *   Redstone signal              — drives switch state via TE tick
 *   Engineer's Wire Cutters      — removes wires via IE's standard path
 *
 * Concrete subclasses only need to provide a registry name and a TE factory.
 */
public abstract class BlockCutoffSwitchBase extends Block implements ITileEntityProvider,
    IRealGridTileEntityProvider
{
    // -----------------------------------------------------------------------
    // Block state properties
    // -----------------------------------------------------------------------

    public static final PropertyDirection FACING =
        PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    /** true = switch CLOSED (power flows); false = switch OPEN (power blocked). */
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    private static final AxisAlignedBB SWITCH_AABB =
        new AxisAlignedBB(0.1875, 0.125, 0.1875, 0.8125, 0.875, 0.8125);

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    protected BlockCutoffSwitchBase(String registryName)
    {
        super(Material.IRON);
        setRegistryName(RealGrid.MODID, registryName);
        setTranslationKey(RealGrid.MODID + "." + registryName);
        setHardness(3.0f);
        setResistance(15.0f);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(ACTIVE, false));
        RealGridRegistry.registerBlock(this);
    }

    // -----------------------------------------------------------------------
    // IRealGridTileEntityProvider — all cutoff switch variants share one TE class
    // -----------------------------------------------------------------------

    @Override
    public String getTileEntityName()
    {
        return "cutoff_switch";
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass()
    {
        return TileEntityCutoffSwitch.class;
    }

    // -----------------------------------------------------------------------
    // Abstract factory
    // -----------------------------------------------------------------------

    /** Creates the tile entity for this specific cutoff switch variant. */
    protected abstract TileEntityCutoffSwitch createSwitchTE();

    // -----------------------------------------------------------------------
    // Internal helper
    // -----------------------------------------------------------------------

    @Nullable
    private TileEntityCutoffSwitch getSwitchTE(IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityCutoffSwitch ? (TileEntityCutoffSwitch) te : null;
    }

    // -----------------------------------------------------------------------
    // Creative / JEI inventory
    // -----------------------------------------------------------------------

    // Only expose meta=0 in creative/JEI to prevent duplicate entries.
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
    }

    // -----------------------------------------------------------------------
    // Block state
    // -----------------------------------------------------------------------

    @Override
    @SuppressWarnings("rawtypes")
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this,
            new IProperty[]{ FACING, ACTIVE },
            new IUnlistedProperty[]{ IEProperties.CONNECTIONS, IEProperties.TILEENTITY_PASSTHROUGH });
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (state instanceof IExtendedBlockState)
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityImmersiveConnectable)
            {
                state = ((IExtendedBlockState) state)
                    .withProperty(IEProperties.CONNECTIONS,
                        ((TileEntityImmersiveConnectable) te).genConnBlockstate())
                    .withProperty(IEProperties.TILEENTITY_PASSTHROUGH, te);
            }
        }
        return state;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState()
            .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
            .withProperty(ACTIVE, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getHorizontalIndex()
            | (state.getValue(ACTIVE) ? 4 : 0);
    }

    /**
     * Reads te.active at render time so the model always reflects the current
     * switch state even when the stored block-state metadata and the TE are
     * momentarily out of sync (e.g. on initial chunk load).
     *
     * This is the primary path the chunk renderer uses to select between the
     * open and closed model variants: the blockstates JSON must have entries
     * for both "active=true" (closed) and "active=false" (open).
     */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntityCutoffSwitch te = getSwitchTE(world, pos);
        return te != null ? state.withProperty(ACTIVE, te.active) : state;
    }

    // -----------------------------------------------------------------------
    // Placement
    // -----------------------------------------------------------------------

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer,
                                            EnumHand hand)
    {
        return getDefaultState()
            .withProperty(FACING, placer.getHorizontalFacing().getOpposite())
            .withProperty(ACTIVE, true);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack)
    {
        if (!world.isRemote)
        {
            TileEntityCutoffSwitch te = getSwitchTE(world, pos);
            if (te != null)
            {
                te.facing = state.getValue(FACING);
                // FIX 1: Sync te.active to the intended initial state (CLOSED = true) that
                // getStateForPlacement declared.  Without this, te.active stays at its Java
                // default of false (OPEN), and getActualState() — which is the rendering
                // authority — immediately shows the switch as permanently open.
                te.active = state.getValue(ACTIVE);
                te.applyStateChange();
            }
        }
    }

    // -----------------------------------------------------------------------
    // Block destruction — clean up IE wire connections
    // -----------------------------------------------------------------------

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote)
        {
            TileEntityCutoffSwitch te = getSwitchTE(world, pos);
            if (te != null)
            {
                te.onBlockDestroyed();
            }
        }
        super.breakBlock(world, pos, state);
    }

    // -----------------------------------------------------------------------
    // Player interaction
    // -----------------------------------------------------------------------

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand,
                                    EnumFacing facing,
                                    float hitX, float hitY, float hitZ)
    {
        if (world.isRemote) return true;

        TileEntityCutoffSwitch te = getSwitchTE(world, pos);
        if (te == null) return false;

        ItemStack heldItem = player.getHeldItem(hand);

        if (Utils.isHammer(heldItem) && player.isSneaking())
        {
            boolean handled = te.hammerUseSide(facing, player, hitX, hitY, hitZ);
            if (handled)
            {
                ChatUtils.sendServerNoSpamMessages(player, new TextComponentTranslation(
                    RealGrid.MODID + ".info.switch_inverted."
                    + (te.inverted ? "off" : "on")));
            }
            return handled;
        }

        // Wire cutters are handled entirely by IE — do not intercept them here.
        if (!Utils.isHammer(heldItem))
        {
            te.active = !te.active;
            te.applyStateChange();
            ChatUtils.sendServerNoSpamMessages(player, new TextComponentTranslation(
                RealGrid.MODID + ".info.cutoff_status."
                + (te.active ? "closed" : "open")));
            return true;
        }

        return false;
    }

    // -----------------------------------------------------------------------
    // Redstone
    // -----------------------------------------------------------------------

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos,
                                Block blockIn, BlockPos fromPos)
    {
        // FIX 2: Propagate a block-update notification so the render state stays
        // coherent after any neighbour change.  We deliberately do NOT modify
        // te.active here — the mere placement of an adjacent block (even another
        // cutoff switch) must never change this switch's open/closed state.
        // Redstone-driven state changes remain the sole responsibility of
        // TileEntityCutoffSwitch.update().
        if (!world.isRemote)
        {
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override public boolean canProvidePower(IBlockState state) { return true; }

    /**
     * FIX 3: Only emit redstone power along the switch's facing axis (the two
     * faces the switch is "wired through").  Previously, power was emitted on
     * all six sides.  Because canProvidePower is true, any block adjacent on a
     * perpendicular face — including another cutoff switch — would receive this
     * signal via world.isBlockPowered().  The TE's update() loop reads that
     * value to implement redstone control, so the spurious side-face power was
     * fighting the manual toggle and forcing the switch permanently open.
     * Restricting emission to the facing axis eliminates the cross-talk.
     */
    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world,
                            BlockPos pos, EnumFacing side)
    {
        TileEntityCutoffSwitch te = getSwitchTE(world, pos);
        if (te == null) return 0;
        EnumFacing facing = state.getValue(FACING);
        if (side != facing && side != facing.getOpposite()) return 0;
        return te.getWeakRSOutput(state, side);
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world,
                              BlockPos pos, EnumFacing side)
    {
        TileEntityCutoffSwitch te = getSwitchTE(world, pos);
        if (te == null) return 0;
        EnumFacing facing = state.getValue(FACING);
        if (side != facing && side != facing.getOpposite()) return 0;
        return te.getStrongRSOutput(state, side);
    }

    // -----------------------------------------------------------------------
    // Geometry and rendering
    // -----------------------------------------------------------------------

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SWITCH_AABB;
    }

    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    // -----------------------------------------------------------------------
    // Client sync event
    // -----------------------------------------------------------------------

    @Override
    public boolean eventReceived(IBlockState state, World world,
                                 BlockPos pos, int id, int param)
    {
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
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return createSwitchTE();
    }
}