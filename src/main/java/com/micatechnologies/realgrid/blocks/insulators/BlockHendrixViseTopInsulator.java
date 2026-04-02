package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Hendrix Vise Top Insulator Block.
 * Modern distribution line insulator. Acts as wire relay.
 */
public class BlockHendrixViseTopInsulator extends BlockInsulatorBase
{
    public BlockHendrixViseTopInsulator()
    {
        super("hendrix_vise_top_insulator");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityHendrixViseTopInsulator();
    }
}
