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

    /**
     * class_c_transformer_top_1wire.json models a single cap centred on X = 8px.
     */
    @Override
    protected double[] getHvBushingOffsets()
    {
        return new double[]{ 0.5 };
    }

    @Override
    protected double getHvBushingHeight()
    {
        return CLASS_C_BUSHING_HEIGHT;
    }
}
