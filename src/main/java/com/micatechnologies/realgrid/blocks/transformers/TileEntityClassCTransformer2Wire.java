package com.micatechnologies.realgrid.blocks.transformers;

/**
 * Class C Transformer - 2-Wire Variant
 * 2 HV connections on the TOP
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

    /**
     * class_c_transformer_top_2wire.json models the cap centres on X = 4px and 12px.
     */
    @Override
    protected double[] getHvBushingOffsets()
    {
        return new double[]{ 0.25, 0.75 };
    }

    @Override
    protected double getHvBushingHeight()
    {
        return CLASS_C_BUSHING_HEIGHT;
    }
}
