package com.micatechnologies.realgrid.blocks.crossarms;

import blusunrize.immersiveengineering.common.util.Utils;
import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.init.RealGridRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * The middle of a back-to-back (double) crossarm, issue #38: two arms on opposite faces of a pole,
 * tied together beside the pole. The arms are ordinary {@link BlockCrossarm} segments; this block
 * goes in the gap between them, next to the pole, where it joins them at the arms' height.
 *
 * <p>On wood arms it is the double-arming bolt, threaded through both arms with a square washer and
 * nut on each arm's front face. On fiberglass arms it is a spacer bracket between them, bolted
 * through, with nothing, square washers or round washers (SCE's practice) on the front faces; the
 * Engineer's Hammer steps through those ({@link #STYLE}).
 *
 * <p>{@link #AXIS} is the way the two arms face (away from each other along it). It needs an arm on
 * each side, facing away, and drops when either goes.
 */
public class BlockCrossarmSpacer extends Block
{
    public static final PropertyEnum<EnumFacing.Axis> AXIS =
        PropertyEnum.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);
    public static final PropertyEnum<Style> STYLE = PropertyEnum.create("style", Style.class);

    /** What is on the arms' front faces where the bolts come through (fiberglass only). */
    public enum Style implements IStringSerializable
    {
        PLAIN("plain"),
        SQUARE("square"),
        ROUND("round");

        private final String name;

        Style(String name)
        {
            this.name = name;
        }

        public Style next()
        {
            return values()[(ordinal() + 1) % values().length];
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    /** Wood arms (a double-arming bolt) or fiberglass ones (a spacer bracket). */
    public enum Kind
    {
        WOOD("wood"),
        FIBERGLASS("fiberglass");

        final String name;

        Kind(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }

    /** The bolt or bracket's box, along z: the full gap between the arms at their height. */
    private static final AxisAlignedBB BOUNDS_Z = new AxisAlignedBB(6 / 16.0, 10 / 16.0, 0, 10 / 16.0, 1, 1);
    private static final AxisAlignedBB BOUNDS_X = new AxisAlignedBB(0, 10 / 16.0, 6 / 16.0, 1, 1, 10 / 16.0);

    private final Kind kind;

    public BlockCrossarmSpacer(Kind kind)
    {
        super(Material.IRON);
        this.kind = kind;
        String registryName = "crossarm_spacer_" + kind.getName();
        setRegistryName(RealGrid.MODID, registryName);
        setTranslationKey(RealGrid.MODID + "." + registryName);
        setHardness(1.5f);
        setResistance(10.0f);
        setDefaultState(blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Z).withProperty(STYLE, Style.PLAIN));
        RealGridRegistry.registerBlock(this);
    }

    public Kind getKind()
    {
        return kind;
    }

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AXIS, STYLE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        Style[] styles = Style.values();
        return getDefaultState().withProperty(AXIS, (meta & 1) == 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X)
            .withProperty(STYLE, styles[Math.min((meta >> 1) & 3, styles.length - 1)]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(AXIS) == EnumFacing.Axis.X ? 1 : 0) | state.getValue(STYLE).ordinal() << 1;
    }

    // -----------------------------------------------------------------------
    // Placement and support
    // -----------------------------------------------------------------------

    /** @return the axis along which {@code pos} has an arm on each side, facing away from it, or null */
    static EnumFacing.Axis betweenArms(IBlockAccess world, BlockPos pos)
    {
        for (EnumFacing f : new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST})
            if (armFacing(world, pos.offset(f), f) && armFacing(world, pos.offset(f.getOpposite()), f.getOpposite()))
                return f.getAxis();
        return null;
    }

    private static boolean armFacing(IBlockAccess world, BlockPos at, EnumFacing facing)
    {
        IBlockState state = world.getBlockState(at);
        return state.getBlock() instanceof BlockCrossarm && state.getValue(BlockCrossarm.FACING) == facing;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        return super.canPlaceBlockAt(world, pos) && betweenArms(world, pos) != null;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand)
    {
        EnumFacing.Axis axis = betweenArms(world, pos);
        return getDefaultState().withProperty(AXIS, axis == null ? EnumFacing.Axis.Z : axis);
    }

    /** It ties two arms together; once either is gone, it drops. */
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        super.neighborChanged(state, world, pos, block, fromPos);
        if (world.isRemote || betweenArms(world, pos) == state.getValue(AXIS))
            return;
        dropBlockAsItem(world, pos, state, 0);
        world.setBlockToAir(pos);
    }

    /** The Engineer's Hammer steps a fiberglass spacer's washers: none, square, round. */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (kind != Kind.FIBERGLASS || !Utils.isHammer(player.getHeldItem(hand)))
            return false;
        if (world.isRemote)
            return true;
        Style next = state.getValue(STYLE).next();
        world.setBlockState(pos, state.withProperty(STYLE, next), 3);
        player.sendStatusMessage(new TextComponentTranslation("realgrid.crossarm_spacer.style." + next.getName()), true);
        return true;
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }

    // -----------------------------------------------------------------------
    // Shape and rendering
    // -----------------------------------------------------------------------

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return state.getValue(AXIS) == EnumFacing.Axis.X ? BOUNDS_X : BOUNDS_Z;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }
}
