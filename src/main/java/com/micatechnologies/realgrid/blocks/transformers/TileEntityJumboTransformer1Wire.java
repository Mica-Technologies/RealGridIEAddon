package com.micatechnologies.realgrid.blocks.transformers;

/**
 * Jumbo Transformer - 1-Wire Variant
 * 1 HV connection on the TOP bushing
 * 1 invisible MV/LV relay point on middle top
 * Total: 2 connection points
 */
public class TileEntityJumboTransformer1Wire extends TileEntityRealTransformer
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
     * jumbo_transformer_top_1wire.json models a single bushing centred on X = 8px. The side spark
     * arrestors at X = 20px are not connection points.
     */
    @Override
    protected double[] getHvBushingOffsets()
    {
        return new double[]{ 0.5 };
    }

    @Override
    protected double getHvBushingHeight()
    {
        return JUMBO_BUSHING_HEIGHT;
    }
}
