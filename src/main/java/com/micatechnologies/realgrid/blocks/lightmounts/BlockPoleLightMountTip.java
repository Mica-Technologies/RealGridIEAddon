package com.micatechnologies.realgrid.blocks.lightmounts;

import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.init.IRealGridTileEntityProvider;
import com.micatechnologies.realgrid.init.RealGridRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * The invisible block at the end of a pole light mount's arm, where its insulator is. It gives the
 * insulator a box to click: a block's box can't reach the up to eight blocks out to the tip. Wire
 * coils clicked here attach to the mount (see {@link TileEntityPoleLightMountTip}). The mount
 * places and removes it, and breaking it breaks the mount. It has no item and draws nothing; the
 * mount's model draws the insulator.
 */
public class BlockPoleLightMountTip extends Block implements ITileEntityProvider, IRealGridTileEntityProvider
{
    /** A small box for when the mount can't be found, so the block can still be broken. */
    private static final AxisAlignedBB ORPHAN_BOX = new AxisAlignedBB(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);

    public BlockPoleLightMountTip()
    {
        super(Material.IRON);
        setRegistryName(RealGrid.MODID, "pole_light_mount_tip");
        setTranslationKey(RealGrid.MODID + ".pole_light_mount_tip");
        setHardness(2.0f);
        setResistance(10.0f);
        RealGridRegistry.registerBlockWithoutItem(this);
    }

    @Override
    public String getTileEntityName()
    {
        return "pole_light_mount_tip";
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass()
    {
        return TileEntityPoleLightMountTip.class;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityPoleLightMountTip();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    /** @return the mount this tip belongs to, if it is still there and still has its tip here */
    @Nullable
    static BlockPos mountOf(IBlockAccess world, BlockPos tip)
    {
        TileEntity te = world.getTileEntity(tip);
        if (!(te instanceof TileEntityPoleLightMountTip))
            return null;
        BlockPos mount = ((TileEntityPoleLightMountTip) te).getMountPos();
        return tip.equals(PoleLightMountGeometry.tipPos(world.getBlockState(mount), mount)) ? mount : null;
    }

    // -----------------------------------------------------------------------
    // Shape: only the insulator can be clicked, and nothing collides
    // -----------------------------------------------------------------------

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        BlockPos mount = mountOf(world, pos);
        if (mount == null)
            return ORPHAN_BOX;
        IBlockState m = world.getBlockState(mount);
        return PoleLightMountGeometry.tipBox(((BlockPoleLightMount) m.getBlock()).getArmLength(),
            m.getValue(BlockPoleLightMount.SWING), m.getValue(BlockPoleLightMount.FACING));
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos) { return true; }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.INVISIBLE; }

    // -----------------------------------------------------------------------
    // Drops and removal: the mount is the real block
    // -----------------------------------------------------------------------

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        BlockPos mount = mountOf(world, pos);
        return mount == null ? ItemStack.EMPTY : new ItemStack(world.getBlockState(mount).getBlock());
    }

    /** A creative player breaking the tip removes the mount without dropping it. */
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        if (!world.isRemote && player.capabilities.isCreativeMode)
        {
            BlockPos mount = mountOf(world, pos);
            if (mount != null)
                world.setBlockToAir(mount);
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    /**
     * Taking the tip away by any other means breaks the mount and drops it. When the mount is the
     * one being removed, or has swung away from here, it no longer claims this tip and is left alone.
     */
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote)
        {
            BlockPos mount = mountOf(world, pos);
            if (mount != null)
                world.destroyBlock(mount, true);
        }
        super.breakBlock(world, pos, state);
    }
}
