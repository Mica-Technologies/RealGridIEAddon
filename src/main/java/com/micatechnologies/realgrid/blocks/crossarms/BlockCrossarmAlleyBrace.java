package com.micatechnologies.realgrid.blocks.crossarms;

import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.init.RealGridRegistry;
import com.micatechnologies.realgrid.util.BoundsUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * The brace of an alley arm, issue #38: a crossarm that reaches out to one side of its pole, where
 * there is no room on the other (an alley, a building), held by one long brace. The arm itself is
 * ordinary {@link BlockCrossarm} segments, so insulators, dead-ends and the rest seat on it as on
 * any arm; this block is the brace, one per arm length (5, 6 or 7 blocks) and material (galvanized
 * steel, wood, fiberglass, to match the arm).
 *
 * <p>It goes where a V-brace goes: in front of the pole, one block below the arm, bolted to the
 * pole's face. Its strap runs up and out to the arm's front face {@link #reach} blocks to one side.
 * Only {@link #FACING} is stored; {@link #RIGHT}, the side it runs to, is worked out from the arm
 * above each time: the side the arm reaches further, right when even. Only the point has a box; the
 * strap overhangs other blocks, as on the pole light mounts.
 */
public class BlockCrossarmAlleyBrace extends Block
{
    public static final PropertyDirection FACING = BlockCrossarm.FACING;
    public static final PropertyBool RIGHT = BlockCrossarm.RIGHT;

    /** Galvanized steel, wood or fiberglass, to match the arm. */
    public enum Kind
    {
        METAL("metal", Material.IRON),
        WOOD("wood", Material.WOOD),
        FIBERGLASS("fiberglass", Material.ROCK);

        final String name;
        final Material material;

        Kind(String name, Material material)
        {
            this.name = name;
            this.material = material;
        }

        public String getName()
        {
            return name;
        }
    }

    /** The arm lengths there are alley braces for. */
    public static final int[] LENGTHS = {5, 6, 7};

    private final Kind kind;
    private final int length;

    public BlockCrossarmAlleyBrace(Kind kind, int length)
    {
        super(kind.material);
        this.kind = kind;
        this.length = length;
        String registryName = "crossarm_alley_brace_" + kind.getName() + "_" + length;
        setRegistryName(RealGrid.MODID, registryName);
        setTranslationKey(RealGrid.MODID + "." + registryName);
        setHardness(1.5f);
        setResistance(10.0f);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(RIGHT, true));
        RealGridRegistry.registerBlock(this);
    }

    public Kind getKind()
    {
        return kind;
    }

    /** @return the arm's length in blocks, pole segment included */
    public int getLength()
    {
        return length;
    }

    /** @return how many blocks out from the pole the strap meets the arm: two short of its far end */
    public static int reach(int length)
    {
        return length - 2;
    }

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, RIGHT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        EnumFacing facing = state.getValue(FACING);
        BlockPos above = pos.up();
        int left = run(world, above, facing, facing.rotateYCCW());
        int right = run(world, above, facing, facing.rotateY());
        return state.withProperty(RIGHT, right >= left);
    }

    /** @return how many arm segments carry on from {@code from} towards {@code way} */
    private static int run(IBlockAccess world, BlockPos from, EnumFacing facing, EnumFacing way)
    {
        int n = 0;
        BlockPos at = from;
        while (n < 16 && BlockCrossarm.continues(world, at, facing, way))
        {
            at = at.offset(way);
            n++;
        }
        return n;
    }

    // -----------------------------------------------------------------------
    // Placement and support
    // -----------------------------------------------------------------------

    /** As the V-brace: under an arm when its underside is clicked, otherwise against the clicked face. */
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand)
    {
        IBlockState clicked = world.getBlockState(pos.offset(side.getOpposite()));
        if (side == EnumFacing.DOWN && clicked.getBlock() instanceof BlockCrossarm)
            return getDefaultState().withProperty(FACING, clicked.getValue(BlockCrossarm.FACING));
        EnumFacing facing = side.getAxis().isHorizontal() ? side : placer.getHorizontalFacing().getOpposite();
        return getDefaultState().withProperty(FACING, facing);
    }

    /** Bolted to the pole; without one behind it, it drops. */
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        super.neighborChanged(state, world, pos, block, fromPos);
        EnumFacing facing = state.getValue(FACING);
        if (world.isRemote || BlockCrossarm.isPole(world, pos.offset(facing.getOpposite()), facing))
            return;
        dropBlockAsItem(world, pos, state, 0);
        world.setBlockToAir(pos);
    }

    // -----------------------------------------------------------------------
    // Shape and rendering
    // -----------------------------------------------------------------------

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        float[] b = BoundsUtil.rotateBounds(BlockCrossarmBrace.POINT_BOUNDS, state.getValue(FACING));
        return new AxisAlignedBB(b[0], b[1], b[2], b[3], b[4], b[5]);
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
