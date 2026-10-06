package com.micatechnologies.realgrid.blocks.cases;

import blusunrize.immersiveengineering.common.util.Utils;
import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.blocks.crossarms.BlockCrossarm;
import com.micatechnologies.realgrid.init.RealGridRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/**
 * A transformer case, issue #38: a platform between poles that jumbo transformers stand on, instead of
 * Immersive Engineering's scaffolding. Steel (as the LADWP builds them) or wood.
 *
 * <p>Each block is one square of deck, at the top of its block so a transformer placed on it stands
 * in the block above. Cases next to each other join into one platform of any size and shape (fence
 * logic: {@link #NORTH}, {@link #EAST}, {@link #SOUTH}, {@link #WEST} say which sides join another
 * case of the same kind, worked out each time). The open edges get a framing beam under the deck, and
 * a railing above it while {@link #RAILINGS} is on; the Engineer's Hammer switches the railings of the
 * whole platform at once. A side against a pole gets neither.
 *
 * <p>The platform is bolted to its poles: it stays up while some square of it is next to a pole or
 * stands on something solid, and otherwise drops.
 */
public class BlockTransformerCase extends Block
{
    public static final PropertyBool RAILINGS = PropertyBool.create("railings");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");

    /** Steel (LADWP) or wood. */
    public enum Kind
    {
        METAL("metal", Material.IRON),
        WOOD("wood", Material.WOOD);

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

    /** The deck: the top 3 px of the block. */
    private static final AxisAlignedBB DECK = new AxisAlignedBB(0, 13 / 16.0, 0, 1, 1, 1);

    /** How many squares of one platform to look through for a pole. */
    private static final int MAX_PLATFORM = 256;

    private final Kind kind;

    public BlockTransformerCase(Kind kind)
    {
        super(kind.material);
        this.kind = kind;
        String registryName = "transformer_case_" + kind.getName();
        setRegistryName(RealGrid.MODID, registryName);
        setTranslationKey(RealGrid.MODID + "." + registryName);
        setHardness(2.0f);
        setResistance(10.0f);
        setDefaultState(blockState.getBaseState().withProperty(RAILINGS, true)
            .withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false));
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
        return new BlockStateContainer(this, RAILINGS, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(RAILINGS, (meta & 1) == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(RAILINGS) ? 1 : 0;
    }

    /** A side joins another case of the same kind; a side against a pole is closed by the pole. */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state
            .withProperty(NORTH, closed(world, pos, EnumFacing.NORTH))
            .withProperty(EAST, closed(world, pos, EnumFacing.EAST))
            .withProperty(SOUTH, closed(world, pos, EnumFacing.SOUTH))
            .withProperty(WEST, closed(world, pos, EnumFacing.WEST));
    }

    /** @return whether the side towards {@code way} has no open edge: another case of this kind, or a pole */
    private boolean closed(IBlockAccess world, BlockPos pos, EnumFacing way)
    {
        BlockPos next = pos.offset(way);
        return world.getBlockState(next).getBlock() == this || BlockCrossarm.isPole(world, next, way.getOpposite());
    }

    // -----------------------------------------------------------------------
    // Support and the hammer
    // -----------------------------------------------------------------------

    /** @return the squares of the platform {@code pos} belongs to: cases of this kind joined side by side */
    private Set<BlockPos> platform(IBlockAccess world, BlockPos pos)
    {
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        seen.add(pos);
        queue.add(pos);
        while (!queue.isEmpty() && seen.size() < MAX_PLATFORM)
        {
            BlockPos at = queue.poll();
            for (EnumFacing way : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos next = at.offset(way);
                if (!seen.contains(next) && world.getBlockState(next).getBlock() == this)
                {
                    seen.add(next);
                    queue.add(next);
                }
            }
        }
        return seen;
    }

    /** @return whether some square of the platform is next to a pole, or stands on something solid */
    private boolean isHeldUp(IBlockAccess world, BlockPos pos)
    {
        for (BlockPos at : platform(world, pos))
        {
            if (world.getBlockState(at.down()).getBlockFaceShape(world, at.down(), EnumFacing.UP) == BlockFaceShape.SOLID)
                return true;
            for (EnumFacing way : EnumFacing.Plane.HORIZONTAL)
                if (world.getBlockState(at.offset(way)).getBlock() != this && BlockCrossarm.isPole(world, at.offset(way), way.getOpposite()))
                    return true;
        }
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        if (!super.canPlaceBlockAt(world, pos))
            return false;
        if (world.getBlockState(pos.down()).getBlockFaceShape(world, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID)
            return true;
        for (EnumFacing way : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos next = pos.offset(way);
            if (world.getBlockState(next).getBlock() == this || BlockCrossarm.isPole(world, next, way.getOpposite()))
                return true;
        }
        return false;
    }

    /** A new square matches the railings of a platform it joins; on its own, it has them. */
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand)
    {
        for (EnumFacing way : EnumFacing.Plane.HORIZONTAL)
        {
            IBlockState next = world.getBlockState(pos.offset(way));
            if (next.getBlock() == this)
                return getDefaultState().withProperty(RAILINGS, next.getValue(RAILINGS));
        }
        return getDefaultState();
    }

    /** Drops the square once its platform has nothing holding it up; the rest of it follows. */
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        super.neighborChanged(state, world, pos, block, fromPos);
        if (world.isRemote || isHeldUp(world, pos))
            return;
        dropBlockAsItem(world, pos, state, 0);
        world.setBlockToAir(pos);
    }

    /** The Engineer's Hammer puts the railings up, or takes them down, all round the platform. */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!Utils.isHammer(player.getHeldItem(hand)))
            return false;
        if (world.isRemote)
            return true;
        boolean railings = !state.getValue(RAILINGS);
        for (BlockPos at : platform(world, pos))
            world.setBlockState(at, world.getBlockState(at).withProperty(RAILINGS, railings), 3);
        player.sendStatusMessage(new TextComponentTranslation(
            "realgrid.transformer_case.railings." + (railings ? "on" : "off")), true);
        return true;
    }

    // -----------------------------------------------------------------------
    // Shape and rendering
    // -----------------------------------------------------------------------

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return DECK;
    }

    /** The deck is a floor: transformers and the player stand on it. */
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.UP;
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
