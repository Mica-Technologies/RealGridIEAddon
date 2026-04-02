package com.micatechnologies.realgrid.blocks.transformers;

/**
 * Class C Transformer - 2-Wire Variant
 * 2 HV connections on the TOP (left and right of top)
 * 1 invisible MV/LV relay point on middle top
 * Total: 3 connection points
 */
public class TileEntityClassCTransformer2Wire extends TileEntityRealTransformer
{
    @Override
    public boolean isTwoWire()
    {
        return true;
    }

    @Override
    public boolean isHvOnSide()
    {
        return false;
    }
}
