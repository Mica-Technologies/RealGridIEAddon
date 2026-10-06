package com.micatechnologies.realgrid.blocks.crossarms;

import net.minecraft.util.IStringSerializable;

/**
 * The sign on a crossarm segment's front face, issue #38: HIGH and VOLTAGE (usually on the segments
 * either side of the pole, reading HIGH VOLTAGE) or N for a neutral, each either an embossed plate
 * nailed on or a sticker. Drawn over the arm, so the arm's own texture stays plain. The Engineer's
 * Hammer steps through them in this order.
 */
public enum CrossarmSign implements IStringSerializable
{
    NONE("none"),
    HIGH_NAILED("high_nailed"),
    VOLTAGE_NAILED("voltage_nailed"),
    N_NAILED("n_nailed"),
    HIGH_STICKER("high_sticker"),
    VOLTAGE_STICKER("voltage_sticker"),
    N_STICKER("n_sticker");

    private final String name;

    CrossarmSign(String name)
    {
        this.name = name;
    }

    public CrossarmSign next()
    {
        return values()[(ordinal() + 1) % values().length];
    }

    public static CrossarmSign byName(String name)
    {
        for (CrossarmSign sign : values())
            if (sign.name.equals(name))
                return sign;
        return NONE;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
