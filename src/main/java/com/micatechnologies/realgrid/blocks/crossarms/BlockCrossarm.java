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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * One segment of a crossarm, issue #38. Segments laid in a row across the face of a pole join into a
 * single arm of any length, flush against the pole and at the top of their blocks, the way a real
 * crossarm is bolted across the pole's face.
 *
 * <p>{@link #FACING} points away from the pole; the arm runs across it, from its left to its right.
 * The rest of the state is worked out from the neighbours each time, never stored, so nothing goes
 * stale when the arm is changed (fence logic): {@link #LEFT} and {@link #RIGHT} say whether the arm
 * carries on that way, and {@link #POLE} whether this segment sits against a pole, where it is
 * through-bolted.
 *
 * <p>Clicking a pole's face puts a segment flush in front of it; clicking either end of an arm adds
 * a segment there, in line with it. An arm stays up while any segment in its unbroken row is against
 * a pole.
 */
public class BlockCrossarm extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool LEFT = PropertyBool.create("left");
    public static final PropertyBool RIGHT = PropertyBool.create("right");
    public static final PropertyBool POLE = PropertyBool.create("pole");

    /** The arm's cross-section, for a north-facing segment: 6 px square at the top back of the block, against the pole. */
    static final float[] ARM_BOUNDS = {0.0f, 10 / 16f, 10 / 16f, 1.0f, 1.0f, 1.0f};

    /** How far along an arm to look for a pole holding it up. */
    private static final int MAX_RUN = 64;

    private final CrossarmMaterial material;

    public BlockCrossarm(CrossarmMaterial material)
    {
        super(material.isFiberglass() ? Material.ROCK : Material.WOOD);
        this.material = material;
        String registryName = "crossarm_" + material.getName();
        setRegistryName(RealGrid.MODID, registryName);
        setTranslationKey(RealGrid.MODID + "." + registryName);
        setHardness(2.0f);
        setResistance(10.0f);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH)
            .withProperty(LEFT, false).withProperty(RIGHT, false).withProperty(POLE, false));
        RealGridRegistry.registerBlock(this);
    }

    public CrossarmMaterial getMaterialType()
    {
        return material;
    }

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, LEFT, RIGHT, POLE);
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
        return state
            .withProperty(LEFT, continues(world, pos, facing, facing.rotateYCCW()))
            .withProperty(RIGHT, continues(world, pos, facing, facing.rotateY()))
            .withProperty(POLE, isPole(world, pos.offset(facing.getOpposite()), facing));
    }

    /** @return whether the arm carries on from {@code pos} towards {@code way}: an arm segment facing the same way */
    static boolean continues(IBlockAccess world, BlockPos pos, EnumFacing facing, EnumFacing way)
    {
        IBlockState next = world.getBlockState(pos.offset(way));
        return next.getBlock() instanceof BlockCrossarm && next.getValue(FACING) == facing;
    }

    /**
     * @param face the pole's face the arm is against
     * @return whether the block at {@code at} is a pole an arm can be bolted to: anything solid on
     *         that face, such as a log, or another mod's pole (CSM's fiberglass poles), whose faces
     *         are not full blocks. RealGrid's own pole hardware is not a pole.
     */
    static boolean isPole(IBlockAccess world, BlockPos at, EnumFacing face)
    {
        IBlockState state = world.getBlockState(at);
        if (state.getBlockFaceShape(world, at, face) == BlockFaceShape.SOLID)
            return true;
        ResourceLocation id = state.getBlock().getRegistryName();
        return id != null && !RealGrid.MODID.equals(id.getNamespace()) && id.getPath().contains("pole");
    }

    // -----------------------------------------------------------------------
    // Placement and support
    // -----------------------------------------------------------------------

    /**
     * Clicking an end of an arm extends it, in line and facing the same way. Clicking any other
     * side face puts a segment against it, facing away from it. A top or bottom face has no side to
     * go on, so the segment faces the player.
     */
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand)
    {
        IBlockState clicked = world.getBlockState(pos.offset(side.getOpposite()));
        if (clicked.getBlock() instanceof BlockCrossarm && side.getAxis() == clicked.getValue(FACING).rotateY().getAxis())
            return getDefaultState().withProperty(FACING, clicked.getValue(FACING));
        EnumFacing facing = side.getAxis().isHorizontal() ? side : placer.getHorizontalFacing().getOpposite();
        return getDefaultState().withProperty(FACING, facing);
    }

    /** @return whether some segment in this segment's unbroken row is against a pole */
    static boolean isHeldUp(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        if (isPole(world, pos.offset(facing.getOpposite()), facing))
            return true;
        for (EnumFacing way : new EnumFacing[]{facing.rotateYCCW(), facing.rotateY()})
        {
            BlockPos at = pos;
            for (int i = 0; i < MAX_RUN && continues(world, at, facing, way); i++)
            {
                at = at.offset(way);
                if (isPole(world, at.offset(facing.getOpposite()), facing))
                    return true;
            }
        }
        return false;
    }

    /** Drops the segment once nothing in its row is against a pole; the rest of the row follows. */
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        super.neighborChanged(state, world, pos, block, fromPos);
        if (world.isRemote || isHeldUp(world, pos, state.getValue(FACING)))
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
        float[] b = BoundsUtil.rotateBounds(ARM_BOUNDS, state.getValue(FACING));
        return new AxisAlignedBB(b[0], b[1], b[2], b[3], b[4], b[5]);
    }

    /** No face of a segment is a full block face, so fences, panes and the like don't join onto it. */
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
