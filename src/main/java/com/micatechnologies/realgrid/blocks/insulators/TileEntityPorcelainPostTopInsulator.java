package com.micatechnologies.realgrid.blocks.insulators;

import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

/**
 * Porcelain Post Top Insulator
 * Classic porcelain insulator with color variants: white (0) and black (1).
 * Acts as a wire relay.
 */
public class TileEntityPorcelainPostTopInsulator extends TileEntityInsulatorBase
{
    /** 0 = white, 1 = black */
    public int colorVariant = 0;

    @Override
    public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setInteger("colorVariant", colorVariant);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        colorVariant = nbt.getInteger("colorVariant");
    }

    @Override
    public Vec3d getConnectionOffset(Connection con)
    {
        // Post top - wire connects at the very top of the porcelain dome
        return new Vec3d(0.5, 0.9375, 0.5);
    }

    @Override
    public float[] getBlockBounds()
    {
        // Porcelain dome shape
        return new float[]{0.25f, 0.0f, 0.25f, 0.75f, 0.9375f, 0.75f};
    }
}
