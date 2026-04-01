package com.micatechnologies.realgridaddon.blocks.transformers;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.common.util.Utils;
import com.micatechnologies.realgridaddon.RealGridAddon;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
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

    protected static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    protected static final AxisAlignedBB UPPER_AABB = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);

    public BlockRealTransformerBase(String registryName)
    {
        super(Material.IRON);
        setRegistryName(RealGridAddon.MODID, registryName);
        setTranslationKey(RealGridAddon.MODID + "." + registryName);
        setHardness(3.0f);
        setResistance(15.0f);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(DUMMY, 0));
    }

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
        int dummyVal = (meta >> 2) & 1;
        EnumFacing facing = EnumFacing.byHorizontalIndex(facingIndex);
        return getDefaultState().withProperty(FACING, facing).withProperty(DUMMY, dummyVal);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int facingIndex = state.getValue(FACING).getHorizontalIndex();
        int dummyVal = state.getValue(DUMMY);
        return facingIndex | (dummyVal << 2);
    }

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
                transformer.dummy = 0;

                // Place dummy block above
                BlockPos upperPos = pos.up();
                if (world.isAirBlock(upperPos))
                {
                    IBlockState upperState = state.withProperty(DUMMY, 1);
                    world.setBlockState(upperPos, upperState);
                    TileEntity upperTe = world.getTileEntity(upperPos);
                    if (upperTe instanceof TileEntityRealTransformer)
                    {
                        ((TileEntityRealTransformer) upperTe).dummy = 1;
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

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityRealTransformer)
        {
            TileEntityRealTransformer transformer = (TileEntityRealTransformer) te;
            int myDummy = transformer.dummy;
            BlockPos basePos = pos.add(0, -myDummy, 0);

            for (int i = 0; i <= 1; i++)
            {
                BlockPos targetPos = basePos.up(i);
                if (!targetPos.equals(pos) && world.getBlockState(targetPos).getBlock() == this)
                {
                    world.setBlockToAir(targetPos);
                }
            }
        }
        if (te instanceof IImmersiveConnectable && !world.isRemote)
        {
            ImmersiveNetHandler.INSTANCE.clearAllConnectionsFor(
                Utils.toCC(te), world, world.getGameRules().getBoolean("doTileDrops"));
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TileEntity te = source.getTileEntity(pos);
        if (te instanceof TileEntityRealTransformer)
        {
            if (((TileEntityRealTransformer) te).dummy == 1)
                return UPPER_AABB;
        }
        return BASE_AABB;
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
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
    {
        TileEntity te = world.getTileEntity(pos);
        return te != null && te.receiveClientEvent(id, param);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }
}
