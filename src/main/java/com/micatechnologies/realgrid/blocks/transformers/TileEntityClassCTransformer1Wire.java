package com.micatechnologies.realgrid.blocks.transformers;

/**
 * Class C Transformer - 1-Wire Variant
 * 1 HV connection on the TOP
 * 1 invisible MV/LV relay point on middle top
 * Total: 2 connection points
 */
public class TileEntityClassCTransformer1Wire extends TileEntityRealTransformer
{
    @Override
    public boolean isTwoWire()
    {
        return false;
    }

    @Override
    public boolean isHvOnSide()
    {
        return false;
    }
}
