package com.micatechnologies.realgridaddon.blocks.transformers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Class C Transformer - 1-Wire Variant Block
 * 1 HV connection on the TOP, 1 invisible MV/LV relay point on top center.
 */
public class BlockClassCTransformer1Wire extends BlockRealTransformerBase
{
    public BlockClassCTransformer1Wire()
    {
        super("class_c_transformer_1wire");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityClassCTransformer1Wire();
    }
}
