package com.micatechnologies.realgrid.blocks.transformers;

/**
 * Class A Transformer - 2-Wire Variant
 * 2 HV connections on the SIDE (left and right of upper block)
 * 1 invisible MV/LV relay point on middle top
 * Total: 3 connection points
 */
public class TileEntityClassATransformer2Wire extends TileEntityRealTransformer
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

    /**
     * class_a_transformer.json models the insulator fins at X = -4.5px / -2.5px on the left arm and
     * 18.5px / 20.5px on the right; the wire meets the top of each arm at the midpoint of its pair.
     */
    @Override
    protected double[] getHvBushingOffsets()
    {
        return new double[]{ CLASS_A_ARM_LEFT, CLASS_A_ARM_RIGHT };
    }

    @Override
    protected double getHvBushingHeight()
    {
        return CLASS_A_ARM_HEIGHT;
    }

    @Override
    protected double getHvBushingDepth()
    {
        return CLASS_A_ARM_DEPTH;
    }
}
