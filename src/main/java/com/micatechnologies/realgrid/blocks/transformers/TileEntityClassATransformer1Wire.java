package com.micatechnologies.realgrid.blocks.transformers;

/**
 * Class A Transformer - 1-Wire Variant
 * 1 HV connection on the SIDE
 * 1 invisible MV/LV relay point on middle top
 * Total: 2 connection points
 */
public class TileEntityClassATransformer1Wire extends TileEntityRealTransformer
{
    @Override
    public boolean isTwoWire()
    {
        return false;
    }

    @Override
    public boolean isHvOnSide()
    {
        return true;
    }
}
