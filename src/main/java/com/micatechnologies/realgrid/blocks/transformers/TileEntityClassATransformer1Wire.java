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

    /**
     * Shares class_a_transformer.json with the 2-wire variant, so both arms are modelled; the one wire goes
     * to whichever arm is nearer where it comes from.
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
