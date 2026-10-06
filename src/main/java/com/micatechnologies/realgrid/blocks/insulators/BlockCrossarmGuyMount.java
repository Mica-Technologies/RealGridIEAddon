package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Guy-span attachment, issue #38: a galvanized eyebolt fixed to a crossarm (or a pole) that takes
 * Immersive Engineering's Structural Rope or Structural Steel Cable, for the guy spans that tie
 * fiberglass arms back. It hangs from its back like a dead-end insulator, so it seats onto an arm's
 * front, back or ends the same way ({@link com.micatechnologies.realgrid.blocks.crossarms.ArmSeat}),
 * and the arm draws an eyebolt where it is fixed.
 */
public class BlockCrossarmGuyMount extends BlockInsulatorBase
{
    public BlockCrossarmGuyMount()
    {
        super("crossarm_guy_mount");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityCrossarmGuyMount();
    }
}
