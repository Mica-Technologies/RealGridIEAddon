package com.micatechnologies.realgrid.blocks.insulators;

import blusunrize.immersiveengineering.api.IEProperties;
import net.minecraft.block.properties.IProperty;
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
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;

/**
 * Abstract base for insulator blocks that support two color variants (meta 0 and 1).
 * Handles the COLOR block state property, meta encoding, sub-blocks, and TE color sync.
 *
 * Concrete subclasses only need to provide the TE factory via {@link #createColoredTE()}.
 */
public abstract class BlockColoredInsulatorBase extends BlockInsulatorBase {

    public static final PropertyInteger COLOR = PropertyInteger.create("color", 0, 1);

    protected BlockColoredInsulatorBase(String registryName) {
        super(registryName);
        setDefaultState(blockState.getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(COLOR, 0));
    }

    /** Creates the TE for this color-variant insulator. Color is set separately. */
    protected abstract TileEntityInsulatorBase createColoredTE();

    @Override
    @SuppressWarnings("rawtypes")
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this,
                new IProperty[]{ FACING, COLOR },
                new IUnlistedProperty[]{ IEProperties.CONNECTIONS });
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byHorizontalIndex(meta & 3);
        int color = (meta >> 2) & 1;
        return getDefaultState().withProperty(FACING, facing).withProperty(COLOR, color);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facing = state.getValue(FACING).getHorizontalIndex();
        int color = state.getValue(COLOR);
        return facing | (color << 2);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing playerFacing = placer.getHorizontalFacing().getOpposite();
        int color = meta > 0 ? 1 : 0;
        return getDefaultState().withProperty(FACING, playerFacing).withProperty(COLOR, color);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityInsulatorBase) {
                ((TileEntityInsulatorBase) te).colorVariant = state.getValue(COLOR);
            }
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(COLOR);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        TileEntityInsulatorBase te = createColoredTE();
        te.colorVariant = (meta >> 2) & 1;
        return te;
    }
}
