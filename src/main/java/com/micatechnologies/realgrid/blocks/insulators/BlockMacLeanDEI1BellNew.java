package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * MacLean Dead End Insulator Block.
 * For terminating wire runs on poles. Acts as wire relay.
 */
public class BlockMacLeanDEI1BellNew extends BlockInsulatorBase
{
    public BlockMacLeanDEI1BellNew()
    {
        super("maclean_porcelain_dead_end_insulator_new");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityMacLeanDEI1BellNew();
    }
}
