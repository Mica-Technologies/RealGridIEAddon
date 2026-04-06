package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Base tile entity for insulator variants that support color variants.
 * Adds colorVariant field and NBT persistence to the standard insulator TE.
 */
public abstract class TileEntityColoredInsulatorBase extends TileEntityInsulatorBase {

    public int colorVariant = 0;

    @Override
    public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setInteger("colorVariant", colorVariant);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        colorVariant = nbt.getInteger("colorVariant");
    }
}
