package com.micatechnologies.realgridaddon.blocks.transformers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Class A Transformer - 2-Wire Variant Block
 * 2 HV connections on the SIDE, 1 invisible MV/LV relay point on top center.
 */
public class BlockClassATransformer2Wire extends BlockRealTransformerBase
{
    public BlockClassATransformer2Wire()
    {
        super("class_a_transformer_2wire");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityClassATransformer2Wire();
    }
}
