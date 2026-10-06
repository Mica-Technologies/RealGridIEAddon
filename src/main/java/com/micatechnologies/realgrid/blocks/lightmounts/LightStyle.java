package com.micatechnologies.realgrid.blocks.lightmounts;

import net.minecraft.util.IStringSerializable;

/**
 * What a 1- or 2-block pole light mount carries at its tip: a CSM street light in the block one up
 * and out (the arm runs into its socket, as on every length), or an Immersive Engineering floodlight
 * on a yoke, standing up into that same block or hanging down into the one below it. Sneaking with
 * the Engineer's Hammer cycles it. Longer arms only carry street lights.
 */
public enum LightStyle implements IStringSerializable
{
    STREET("street"),
    FLOOD_UP("flood_up"),
    FLOOD_DOWN("flood_down");

    private final String name;

    LightStyle(String name)
    {
        this.name = name;
    }

    /** @return true for the floodlight styles */
    public boolean isFloodlight()
    {
        return this != STREET;
    }

    public LightStyle next()
    {
        return values()[(ordinal() + 1) % values().length];
    }

    public static LightStyle byName(String name)
    {
        for (LightStyle s : values())
            if (s.name.equals(name))
                return s;
        return STREET;
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
