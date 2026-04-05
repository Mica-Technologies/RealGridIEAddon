package com.micatechnologies.realgrid.blocks.transformers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Jumbo Transformer - 2-Wire Variant Block
 * 2 HV connections on the SIDE, 1 invisible MV/LV relay point on top center.
 */
public class BlockJumboTransformer2Wire extends BlockRealTransformerBase
{
    public BlockJumboTransformer2Wire()
    {
        super("jumbo_transformer_2wire");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityJumboTransformer2Wire();
    }
}
