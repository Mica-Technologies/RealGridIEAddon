package com.micatechnologies.realgrid.blocks.transformers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Class C Transformer - 2-Wire Variant Block
 * 2 HV connections on the TOP, 1 invisible MV/LV relay point on top center.
 */
public class BlockClassCTransformer2Wire extends BlockRealTransformerBase
{
    public BlockClassCTransformer2Wire()
    {
        super("class_c_transformer_2wire");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityClassCTransformer2Wire();
    }
}
