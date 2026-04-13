package com.micatechnologies.realgrid.blocks.transformers;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import com.micatechnologies.realgrid.RealGrid;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;

/**
 * Abstract base block for all transformer variants.
 * 2 blocks tall, directional, has associated TileEntity.
 */
public abstract class BlockRealTransformerBase extends Block implements ITileEntityProvider
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyInteger DUMMY = PropertyInteger.create("dummy", 0, 1);

    protected static final AxisAlignedBB BASE_AABB  = new AxisAlignedBB(0.0,    0.0, 0.0,    1.0,    1.0, 1.0);
    protected static final AxisAlignedBB UPPER_AABB = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);

    public BlockRealTransformerBase(String registryName)
    {
        super(Material.IRON);
        setRegistryName(RealGrid.MODID, registryName);
        setTranslationKey(RealGrid.MODID + "." + registryName);
        setHardness(3.0f);
        setResistance(15.0f);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(DUMMY, 0));
    }

    // -----------------------------------------------------------------------
    // Creative / JEI inventory
    // -----------------------------------------------------------------------

    // FIX (duplicate inventory): The DUMMY property encodes two sets of metadata:
    //   meta 0-3 → DUMMY=0, FACING=N/E/S/W  (base / bottom half — placeable item)
    //   meta 4-7 → DUMMY=1, FACING=N/E/S/W  (upper dummy half — world-only, never an item)
    //
    // Without this override, inventory mods such as JEI iterate ALL valid metadata
    // values and list 8 entries — including 4 "dummy upper" variants that look
    // identical to the real transformer but place a broken half-block. This was the
    // root cause of the duplicate inventory issue that persisted after ModBlocks fixes.
    //
    // By returning a single ItemStack at damage=0 (FACING=NORTH, DUMMY=0) we
    // guarantee exactly one creative/JEI entry, matching the one registered in
    // ModBlocks.ITEM_BLOCKS, and prevent the dummy states from ever appearing
    // as pickable items.
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
    }

    // FIX (duplicate item drop): Without this override, breaking the upper (DUMMY=1)
    // block directly causes Minecraft's default harvestBlock() to drop one transformer
    // item for that half. Combined with breakBlock() calling setBlockToAir() on the
    // paired half (no drop), the net result is still one item total — but breaking
    // the DUMMY=0 base block ALSO drops one item, so a two-step break (upper then
    // lower before breakBlock removes both) could yield two drops. More importantly,
    // creative players can obtain the "dummy upper half" item via middle-click, which
    // creates a meta=4 ItemStack that places only the upper block state — a broken
    // one-block transformer with no base.
    //
    // Returning 0 drops for DUMMY=1 completely prevents the dummy half from ever
    // producing an item, matching how vanilla handles the upper half of a door.
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world,
                         BlockPos pos, IBlockState state, int fortune)
    {
        if (state.getValue(DUMMY) == 0)
        {
            super.getDrops(drops, world, pos, state, fortune);
        }
        // DUMMY=1: upper half drops nothing; the base half handles the single drop.
    }

    // -----------------------------------------------------------------------
    // Block state
    // -----------------------------------------------------------------------

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[]{FACING, DUMMY}, new IUnlistedProperty[]{IEProperties.CONNECTIONS});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (state instanceof IExtendedBlockState)
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityImmersiveConnectable)
            {
                state = ((IExtendedBlockState) state).withProperty(IEProperties.CONNECTIONS,
                    ((TileEntityImmersiveConnectable) te).genConnBlockstate());
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
        int facingIndex = meta & 3;
        int dummyVal    = (meta >> 2) & 1;
        EnumFacing facing = EnumFacing.byHorizontalIndex(facingIndex);
        return getDefaultState().withProperty(FACING, facing).withProperty(DUMMY, dummyVal);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int facingIndex = state.getValue(FACING).getHorizontalIndex();
        int dummyVal    = state.getValue(DUMMY);
        return facingIndex | (dummyVal << 2);
    }

    // -----------------------------------------------------------------------
    // Placement
    // -----------------------------------------------------------------------

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand)
    {
        EnumFacing playerFacing = placer.getHorizontalFacing().getOpposite();
        return getDefaultState().withProperty(FACING, playerFacing).withProperty(DUMMY, 0);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack)
    {
        if (!world.isRemote)
        {
            EnumFacing facing = state.getValue(FACING);
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityRealTransformer)
            {
                TileEntityRealTransformer transformer = (TileEntityRealTransformer) te;
                transformer.facing = facing;
                transformer.dummy  = 0;

                // Place dummy block above
                BlockPos upperPos = pos.up();
                if (world.isAirBlock(upperPos))
                {
                    IBlockState upperState = state.withProperty(DUMMY, 1);
                    world.setBlockState(upperPos, upperState);
                    TileEntity upperTe = world.getTileEntity(upperPos);
                    if (upperTe instanceof TileEntityRealTransformer)
                    {
                        ((TileEntityRealTransformer) upperTe).dummy  = 1;
                        ((TileEntityRealTransformer) upperTe).facing = facing;
                    }
                }
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        return super.canPlaceBlockAt(world, pos) && world.isAirBlock(pos.up());
    }

    // -----------------------------------------------------------------------
    // Block destruction
    // -----------------------------------------------------------------------

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityRealTransformer)
        {
            TileEntityRealTransformer transformer = (TileEntityRealTransformer) te;
            int myDummy  = transformer.dummy;
            BlockPos basePos = pos.add(0, -myDummy, 0);

            // Clean up IE wire connections on both halves before removing blocks.
            // Must happen while TileEntities still exist at their positions.
            if (!world.isRemote)
            {
                for (int i = 0; i <= 1; i++)
                {
                    BlockPos cleanupPos = basePos.up(i);
                    TileEntity cleanupTe = world.getTileEntity(cleanupPos);
                    if (cleanupTe instanceof TileEntityRealTransformer)
                    {
                        ((TileEntityRealTransformer) cleanupTe).onBlockDestroyed();
                    }
                }
            }

            for (int i = 0; i <= 1; i++)
            {
                BlockPos targetPos = basePos.up(i);
                if (!targetPos.equals(pos) && world.getBlockState(targetPos).getBlock() == this)
                {
                    world.setBlockToAir(targetPos);
                }
            }
        }
        super.breakBlock(world, pos, state);
    }

    // -----------------------------------------------------------------------
    // Geometry and rendering
    // -----------------------------------------------------------------------

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TileEntity te = source.getTileEntity(pos);
        if (te instanceof TileEntityRealTransformer)
        {
            if (((TileEntityRealTransformer) te).dummy == 1) return UPPER_AABB;
        }
        return BASE_AABB;
    }

    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state)   { return false; }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
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
    public abstract TileEntity createNewTileEntity(World world, int meta);
}
