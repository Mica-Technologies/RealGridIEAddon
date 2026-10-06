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
 * A V-brace under a crossarm, issue #38: wood or galvanized steel, for wooden arms. It goes in front
 * of the pole one block below the arm. Its point is bolted to the pole's face, and a leg runs up
 * to the arm's front face, one block out, on each side where the arm carries on that far.
 *
 * <p>Only {@link #FACING} (away from the pole, as the arm's) is stored. {@link #LEFT} and
 * {@link #RIGHT}, whether each leg is drawn, are worked out from the arm above each time (fence
 * logic): a leg reaches up to the arm segment above and to that side, so it shows only while that
 * segment is there. Only the point has a box; the legs overhang other blocks.
 */
public class BlockCrossarmBrace extends Block
{
    public static final PropertyDirection FACING = BlockCrossarm.FACING;
    public static final PropertyBool LEFT = BlockCrossarm.LEFT;
    public static final PropertyBool RIGHT = BlockCrossarm.RIGHT;

    /** Wood straps or galvanized steel ones. */
    public enum Kind
    {
        WOOD("wood", Material.WOOD),
        METAL("metal", Material.IRON);

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

    /** Round the point on the pole's face, for a north-facing brace (the pole is to the south). */
    static final float[] POINT_BOUNDS = {5 / 16f, 7 / 16f, 12 / 16f, 11 / 16f, 1.0f, 1.0f};

    private final Kind kind;

    public BlockCrossarmBrace(Kind kind)
    {
        super(kind.material);
        this.kind = kind;
        String registryName = "crossarm_brace_" + kind.getName();
        setRegistryName(RealGrid.MODID, registryName);
        setTranslationKey(RealGrid.MODID + "." + registryName);
        setHardness(1.5f);
        setResistance(10.0f);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH)
            .withProperty(LEFT, false).withProperty(RIGHT, false));
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
        return new BlockStateContainer(this, FACING, LEFT, RIGHT);
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
        return state
            .withProperty(LEFT, isArm(world, above.offset(facing.rotateYCCW()), facing))
            .withProperty(RIGHT, isArm(world, above.offset(facing.rotateY()), facing));
    }

    private static boolean isArm(IBlockAccess world, BlockPos at, EnumFacing facing)
    {
        IBlockState state = world.getBlockState(at);
        return state.getBlock() instanceof BlockCrossarm && state.getValue(BlockCrossarm.FACING) == facing;
    }

    // -----------------------------------------------------------------------
    // Placement and support
    // -----------------------------------------------------------------------

    /**
     * Clicking the underside of an arm puts the brace below it, facing the same way. Clicking a
     * pole's face puts it against that face. A top face has no side to go on, so the brace faces
     * the player.
     */
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

    /** The brace is bolted to the pole; without one behind it, it drops. */
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
        float[] b = BoundsUtil.rotateBounds(POINT_BOUNDS, state.getValue(FACING));
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
