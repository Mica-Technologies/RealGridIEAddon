package com.micatechnologies.realgrid.blocks.lightmounts;

import net.minecraft.util.IStringSerializable;

/**
 * Which side of the arm a side-mounted tip insulator stands out of, seen looking along the arm
 * from the pole. Only the 5-block arm mounts its insulator on a side; every other length has it
 * on top, and this property changes nothing for them.
 */
public enum InsulatorSide implements IStringSerializable
{
    LEFT("left"),
    RIGHT("right");

    private final String name;

    InsulatorSide(String name)
    {
        this.name = name;
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
