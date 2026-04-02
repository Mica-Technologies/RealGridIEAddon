package com.micatechnologies.realgrid.blocks.transformers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Class A Transformer - 1-Wire Variant Block
 * 1 HV connection on the SIDE, 1 invisible MV/LV relay point on top center.
 */
public class BlockClassATransformer1Wire extends BlockRealTransformerBase
{
    public BlockClassATransformer1Wire()
    {
        super("class_a_transformer_1wire");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityClassATransformer1Wire();
    }
}
