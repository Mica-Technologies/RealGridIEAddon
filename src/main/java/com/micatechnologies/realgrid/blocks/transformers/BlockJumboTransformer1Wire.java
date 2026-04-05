package com.micatechnologies.realgrid.blocks.transformers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Jumbo Transformer - 1-Wire Variant Block
 * 1 HV connection on the SIDE, 1 invisible MV/LV relay point on top center.
 */
public class BlockJumboTransformer1Wire extends BlockRealTransformerBase
{
    public BlockJumboTransformer1Wire()
    {
        super("jumbo_transformer_1wire");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityJumboTransformer1Wire();
    }
}
