package com.micatechnologies.realgrid.blocks.insulators;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.WireType;

/**
 * The guy-span attachment's wire end (see {@link BlockCrossarmGuyMount}). It carries no power: it
 * takes only Immersive Engineering's structural cables, Structural Rope or Structural Steel Cable,
 * one type at a time, as many as are run to it. The three-argument form IE's coil calls is forwarded
 * to this rule by {@link TileEntityInsulatorBase}.
 */
public class TileEntityCrossarmGuyMount extends TileEntityInsulatorBase
{
    public TileEntityCrossarmGuyMount()
    {
        super(InsulatorGeometry.DEAD_END);
    }

    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target)
    {
        if (cableType != WireType.STRUCTURE_ROPE && cableType != WireType.STRUCTURE_STEEL)
            return false;
        return limitType == null || limitType == cableType;
    }
}
