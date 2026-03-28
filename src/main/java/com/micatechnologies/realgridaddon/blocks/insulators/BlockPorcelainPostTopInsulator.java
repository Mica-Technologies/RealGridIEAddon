package com.micatechnologies.realgridaddon.blocks.insulators;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Porcelain Post Top Insulator Block.
 * Classic porcelain insulator with color variants: white (meta 0) and black (meta 1).
 * Acts as wire relay.
 */
public class BlockPorcelainPostTopInsulator extends BlockInsulatorBase
{
    public static final PropertyInteger COLOR = PropertyInteger.create("color", 0, 1);

    public BlockPorcelainPostTopInsulator()
    {
        super("porcelain_post_top_insulator");
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(COLOR, 0));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, COLOR);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.getHorizontal(meta & 3);
        int color = (meta >> 2) & 1;
        return getDefaultState().withProperty(FACING, facing).withProperty(COLOR, color);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int facing = state.getValue(FACING).getHorizontalIndex();
        int color = state.getValue(COLOR);
        return facing | (color << 2);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand)
    {
        EnumFacing playerFacing = placer.getHorizontalFacing().getOpposite();
        int color = meta > 0 ? 1 : 0;
        return getDefaultState().withProperty(FACING, playerFacing).withProperty(COLOR, color);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote)
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityPorcelainPostTopInsulator)
            {
                ((TileEntityPorcelainPostTopInsulator) te).colorVariant = state.getValue(COLOR);
            }
        }
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(COLOR);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this, 1, 0)); // White
        items.add(new ItemStack(this, 1, 1)); // Black
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        TileEntityPorcelainPostTopInsulator te = new TileEntityPorcelainPostTopInsulator();
        te.colorVariant = (meta >> 2) & 1;
        return te;
    }
}
