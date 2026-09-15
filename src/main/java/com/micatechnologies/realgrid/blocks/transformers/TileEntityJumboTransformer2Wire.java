package com.micatechnologies.realgrid.blocks.transformers;

/**
 * Jumbo Transformer - 2-Wire Variant
 * 2 HV connections on the TOP bushings
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
        return false;
    }

    /**
     * jumbo_transformer_top_2wire.json models the bushings on X = 4px and 12px. The side spark
     * arrestors at X = -5px / 20px are not connection points.
     */
    @Override
    protected double[] getHvBushingOffsets()
    {
        return new double[]{ 0.25, 0.75 };
    }

    @Override
    protected double getHvBushingHeight()
    {
        return JUMBO_BUSHING_HEIGHT;
    }
}
