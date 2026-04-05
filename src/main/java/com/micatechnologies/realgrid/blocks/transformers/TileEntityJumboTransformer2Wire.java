package com.micatechnologies.realgrid.blocks.transformers;

/**
 * Jumbo Transformer - 2-Wire Variant
 * 2 HV connections on the SIDE (left and right of upper block)
 * 1 invisible MV/LV relay point on middle top
 * Total: 3 connection points
 */
public class TileEntityJumboTransformer2Wire extends TileEntityRealTransformer
{
    @Override
    public boolean isTwoWire()
    {
        return true;
    }

    @Override
    public boolean isHvOnSide()
    {
        return true;
    }
}
