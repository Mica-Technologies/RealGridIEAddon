package com.micatechnologies.realgrid.blocks.bankmounts;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

/**
 * Which block of a three-transformer bank mount this is. The centre is bolted to the pole and carries
 * the front tank; the two arms reach out either side to the side tanks. Left and right are seen
 * looking out from the pole, along the mount's facing. Two-transformer brackets are always
 * {@link #CENTER}.
 */
public enum BankMountPart implements IStringSerializable
{
    CENTER("center"),
    LEFT("left"),
    RIGHT("right");

    private final String name;

    BankMountPart(String name)
    {
        this.name = name;
    }

    /** @return the way from the centre to this part, for a mount facing {@code facing}; null for the centre */
    public EnumFacing sideOf(EnumFacing facing)
    {
        switch (this)
        {
            case LEFT:  return facing.rotateYCCW();
            case RIGHT: return facing.rotateY();
            default:    return null;
        }
    }

    public static BankMountPart byIndex(int index)
    {
        BankMountPart[] values = values();
        return index >= 0 && index < values.length ? values[index] : CENTER;
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
