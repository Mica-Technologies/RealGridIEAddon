package com.micatechnologies.realgrid.blocks.lightmounts;

import net.minecraft.util.IStringSerializable;

/**
 * Which way a pole light mount's arm swings at its bracket, seen from above looking out along the
 * mount's facing. The bracket stays flat on the pole in every case; only the arm turns.
 */
public enum ArmSwing implements IStringSerializable
{
    STRAIGHT("straight"),
    /** Anticlockwise from the facing: a north-facing mount reaches north-west. */
    LEFT("left"),
    /** Clockwise from the facing: a north-facing mount reaches north-east. */
    RIGHT("right");

    private final String name;

    ArmSwing(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    /** The order the Engineer's Hammer steps through. */
    public ArmSwing next()
    {
        switch (this)
        {
            case STRAIGHT: return RIGHT;
            case RIGHT:    return LEFT;
            default:       return STRAIGHT;
        }
    }

    public static ArmSwing byIndex(int index)
    {
        ArmSwing[] values = values();
        return index >= 0 && index < values.length ? values[index] : STRAIGHT;
    }
}
