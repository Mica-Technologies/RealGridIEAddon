package com.micatechnologies.realgridaddon.blocks.switchgear;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.common.util.ChatUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import com.micatechnologies.realgridaddon.RealGridAddon;
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
 * Distribution Switchgear Block (MacLean Triple Action Air Release Switch)
 * Single block. Has FACING and ACTIVE properties.
 * Redstone-controlled only. Sneak+hammer toggles inverted mode.
 */
public class BlockDistributionSwitch extends Block implements ITileEntityProvider
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    private static final AxisAlignedBB SWITCH_AABB = new AxisAlignedBB(0.1875, 0.125, 0.1875, 0.8125, 0.875, 0.8125);

    public BlockDistributionSwitch()
    {
        super(Material.IRON);
        setRegistryName(RealGridAddon.MODID, "distribution_switch");
        setTranslationKey(RealGridAddon.MODID + ".distribution_switch");
        setHardness(3.0f);
        setResistance(15.0f);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(ACTIVE, true));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[]{FACING, ACTIVE}, new IUnlistedProperty[]{IEProperties.CONNECTIONS});
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
        EnumFacing facing = EnumFacing.byHorizontalIndex(meta & 3);
        boolean active = (meta & 4) != 0;
        return getDefaultState().withProperty(FACING, facing).withProperty(ACTIVE, active);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int facing = state.getValue(FACING).getHorizontalIndex();
        int active = state.getValue(ACTIVE) ? 4 : 0;
        return facing | active;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityDistributionSwitch)
        {
            return state.withProperty(ACTIVE, ((TileEntityDistributionSwitch) te).active);
        }
        return state;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand)
    {
        EnumFacing playerFacing = placer.getHorizontalFacing().getOpposite();
        return getDefaultState().withProperty(FACING, playerFacing).withProperty(ACTIVE, true);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack)
    {
        if (!world.isRemote)
        {
            EnumFacing facing = state.getValue(FACING);
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityDistributionSwitch)
            {
                ((TileEntityDistributionSwitch) te).facing = facing;
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityDistributionSwitch)
        {
            TileEntityDistributionSwitch switchTe = (TileEntityDistributionSwitch) te;
            ItemStack heldItem = player.getHeldItem(hand);

            // Only hammer interaction: sneak+hammer toggles inverted mode
            if (Utils.isHammer(heldItem) && player.isSneaking())
            {
                if (!world.isRemote)
                {
                    switchTe.inverted = !switchTe.inverted;
                    ChatUtils.sendServerNoSpamMessages(player,
                        new TextComponentTranslation(
                            RealGridAddon.MODID + ".info.switch_inverted." +
                            (switchTe.inverted ? "on" : "off")));
                    if (switchTe.wires > 1)
                    {
                        ImmersiveNetHandler.INSTANCE.resetCachedIndirectConnections();
                    }
                    switchTe.notifyNeighbours();
                }
                return true;
            }
            // No manual toggle - redstone only
        }
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos,
                                Block blockIn, BlockPos fromPos)
    {
        // Redstone changes are handled by the tile entity's update() tick
    }

    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityDistributionSwitch)
        {
            return ((TileEntityDistributionSwitch) te).getWeakRSOutput(state, side);
        }
        return 0;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityDistributionSwitch)
        {
            return ((TileEntityDistributionSwitch) te).getStrongRSOutput(state, side);
        }
        return 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SWITCH_AABB;
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

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityDistributionSwitch();
    }
}
