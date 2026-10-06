package com.micatechnologies.realgrid.blocks.crossarms;

import blusunrize.immersiveengineering.common.util.Utils;
import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitchBase;
import com.micatechnologies.realgrid.blocks.insulators.BlockInsulatorBase;
import com.micatechnologies.realgrid.init.IRealGridTileEntityProvider;
import com.micatechnologies.realgrid.init.RealGridRegistry;
import com.micatechnologies.realgrid.util.BoundsUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * One segment of a crossarm, issue #38. Segments laid in a row across the face of a pole join into a
 * single arm of any length, flush against the pole and at the top of their blocks, the way a real
 * crossarm is bolted across the pole's face.
 *
 * <p>{@link #FACING} points away from the pole; the arm runs across it, from its left to its right.
 * The rest of the state is worked out from the neighbours each time, never stored, so nothing goes
 * stale when the arm is changed (fence logic): {@link #LEFT} and {@link #RIGHT} say whether the arm
 * carries on that way, {@link #POLE} whether this segment sits against a pole, where it is
 * through-bolted. The hardware for what is mounted on the arm appears by itself, as on a real arm:
 * {@link #PIN} draws an insulator pin's nut under the arm when a top-mounted insulator stands on the
 * segment, {@link #HANGER} a bracket down to an IE connector hung below it, and the fittings
 * ({@link #FRONT_FIT}, {@link #BACK_FIT}, {@link #LEFT_FIT}, {@link #RIGHT_FIT}) an eyebolt where a
 * dead-end or side-mount insulator or a cutoff switch hangs off that face of the segment (see
 * {@link ArmSeat}).
 *
 * <p>What the neighbours can't tell is kept in {@link TileEntityCrossarm}: the {@link #SIGN} on the
 * segment's front face and, on wood arms, the metal {@link #CAPS} on its open ends, both set with the
 * Engineer's Hammer.
 *
 * <p>Clicking a pole's face puts a segment flush in front of it; clicking either end of an arm adds
 * a segment there, in line with it. An arm stays up while any segment in its unbroken row is against
 * a pole.
 */
public class BlockCrossarm extends Block implements IRealGridTileEntityProvider
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool LEFT = PropertyBool.create("left");
    public static final PropertyBool RIGHT = PropertyBool.create("right");
    public static final PropertyBool POLE = PropertyBool.create("pole");
    public static final PropertyBool PIN = PropertyBool.create("pin");
    public static final PropertyBool HANGER = PropertyBool.create("hanger");
    public static final PropertyBool FRONT_FIT = PropertyBool.create("front_fit");
    public static final PropertyBool BACK_FIT = PropertyBool.create("back_fit");
    public static final PropertyBool LEFT_FIT = PropertyBool.create("left_fit");
    public static final PropertyBool RIGHT_FIT = PropertyBool.create("right_fit");
    public static final PropertyEnum<CrossarmSign> SIGN = PropertyEnum.create("sign", CrossarmSign.class);
    public static final PropertyBool CAPS = PropertyBool.create("caps");

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
            .withProperty(LEFT, false).withProperty(RIGHT, false).withProperty(POLE, false)
            .withProperty(PIN, false).withProperty(HANGER, false)
            .withProperty(FRONT_FIT, false).withProperty(BACK_FIT, false)
            .withProperty(LEFT_FIT, false).withProperty(RIGHT_FIT, false)
            .withProperty(SIGN, CrossarmSign.NONE).withProperty(CAPS, false));
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
        return new BlockStateContainer(this, FACING, LEFT, RIGHT, POLE, PIN, HANGER, FRONT_FIT, BACK_FIT, LEFT_FIT, RIGHT_FIT,
            SIGN, CAPS);
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
        TileEntityCrossarm te = tileAt(world, pos);
        return state
            .withProperty(LEFT, continues(world, pos, facing, facing.rotateYCCW()))
            .withProperty(RIGHT, continues(world, pos, facing, facing.rotateY()))
            .withProperty(POLE, isPole(world, pos.offset(facing.getOpposite()), facing))
            .withProperty(PIN, hasPinInsulator(world, pos.up()))
            .withProperty(HANGER, isConnector(world, pos.down()))
            .withProperty(FRONT_FIT, hangsOff(world, pos, facing))
            .withProperty(BACK_FIT, hangsOff(world, pos, facing.getOpposite()))
            .withProperty(LEFT_FIT, hangsOff(world, pos, facing.rotateYCCW()))
            .withProperty(RIGHT_FIT, hangsOff(world, pos, facing.rotateY()))
            .withProperty(SIGN, te == null ? CrossarmSign.NONE : te.getSign())
            .withProperty(CAPS, te != null && te.hasCaps() && !material.isFiberglass());
    }

    /** @return the segment's tile entity, without making one while a chunk is being drawn */
    private static TileEntityCrossarm tileAt(IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world instanceof ChunkCache
            ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK)
            : world.getTileEntity(pos);
        return te instanceof TileEntityCrossarm ? (TileEntityCrossarm) te : null;
    }

    /**
     * @return whether a block hung from its back (a dead-end or side-mount insulator, a cutoff switch)
     *         is fixed to this segment's face towards {@code way}: next to it that way, facing away
     */
    static boolean hangsOff(IBlockAccess world, BlockPos pos, EnumFacing way)
    {
        IBlockState state = world.getBlockState(pos.offset(way));
        Block block = state.getBlock();
        // The two kinds of block each have their own facing property.
        if (block instanceof BlockCutoffSwitchBase)
            return state.getValue(BlockCutoffSwitchBase.FACING) == way;
        return block instanceof BlockInsulatorBase && !((BlockInsulatorBase) block).isTopMount()
            && state.getValue(BlockInsulatorBase.FACING) == way;
    }

    /** @return whether a top-mounted insulator stands at {@code at}, pinned through the arm below it */
    static boolean hasPinInsulator(IBlockAccess world, BlockPos at)
    {
        IBlockState state = world.getBlockState(at);
        return state.getBlock() instanceof BlockInsulatorBase && ((BlockInsulatorBase) state.getBlock()).isTopMount();
    }

    /** @return whether an Immersive Engineering connector hangs at {@code at}, under the arm */
    static boolean isConnector(IBlockAccess world, BlockPos at)
    {
        ResourceLocation id = world.getBlockState(at).getBlock().getRegistryName();
        return id != null && "immersiveengineering".equals(id.getNamespace()) && "connector".equals(id.getPath());
    }

    /**
     * @return whether the arm carries on from {@code pos} towards {@code way}: a segment of the same
     *         material facing the same way (arms are not made of two materials)
     */
    static boolean continues(IBlockAccess world, BlockPos pos, EnumFacing facing, EnumFacing way)
    {
        IBlockState next = world.getBlockState(pos.offset(way));
        return next.getBlock() instanceof BlockCrossarm && next.getBlock() == world.getBlockState(pos).getBlock()
            && next.getValue(FACING) == facing;
    }

    /**
     * @param face the pole's face the arm is against
     * @return whether the block at {@code at} is a pole an arm can be bolted to: anything solid on
     *         that face, such as a log, or another mod's pole (CSM's fiberglass poles), whose faces
     *         are not full blocks. RealGrid's own pole hardware is not a pole.
     */
    public static boolean isPole(IBlockAccess world, BlockPos at, EnumFacing face)
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
    // Signs and end caps
    // -----------------------------------------------------------------------

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityCrossarm();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass()
    {
        return TileEntityCrossarm.class;
    }

    @Override
    public String getTileEntityName()
    {
        return "crossarm";
    }

    /**
     * The Engineer's Hammer steps the segment's sign (none, HIGH, VOLTAGE, N, nailed then sticker);
     * sneaking, it puts the metal end caps on a wood arm's open ends, or takes them off.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!Utils.isHammer(player.getHeldItem(hand)))
            return false;
        if (world.isRemote)
            return true;
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityCrossarm))
            return true;
        TileEntityCrossarm te = (TileEntityCrossarm) tile;
        if (player.isSneaking())
        {
            if (material.isFiberglass())
            {
                player.sendStatusMessage(new TextComponentTranslation("realgrid.crossarm.caps.fiberglass"), true);
                return true;
            }
            te.setCaps(!te.hasCaps());
            player.sendStatusMessage(new TextComponentTranslation("realgrid.crossarm.caps." + (te.hasCaps() ? "on" : "off")), true);
            return true;
        }
        CrossarmSign next = te.getSign().next();
        te.setSign(next);
        player.sendStatusMessage(new TextComponentTranslation("realgrid.crossarm.sign." + next.getName()), true);
        return true;
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
