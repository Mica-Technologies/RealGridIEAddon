package com.micatechnologies.realgridaddon.blocks.insulators;

import com.micatechnologies.realgridaddon.RealGridAddon;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Abstract base block for all insulator variants.
 * Single block, directional, with associated TileEntity.
 */
public abstract class BlockInsulatorBase extends Block implements ITileEntityProvider
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    protected static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.875, 0.75);

    public BlockInsulatorBase(String registryName)
    {
        super(Material.ROCK);
        setRegistryName(RealGridAddon.MODID, registryName);
        setTranslationKey(RealGridAddon.MODID + "." + registryName);
        setHardness(1.5f);
        setResistance(10.0f);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.byHorizontalIndex(meta & 3);
        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand)
    {
        EnumFacing playerFacing = placer.getHorizontalFacing().getOpposite();
        return getDefaultState().withProperty(FACING, playerFacing);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack)
    {
        if (!world.isRemote)
        {
            EnumFacing facing = state.getValue(FACING);
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityInsulatorBase)
            {
                ((TileEntityInsulatorBase) te).facing = facing;
            }
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TileEntity te = source.getTileEntity(pos);
        if (te instanceof TileEntityInsulatorBase)
        {
            float[] bounds = ((TileEntityInsulatorBase) te).getBlockBounds();
            if (bounds != null)
            {
                return new AxisAlignedBB(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
            }
        }
        return DEFAULT_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }
}
