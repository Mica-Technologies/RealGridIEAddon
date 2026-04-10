package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Hendrix Vise Top Insulator Block.
 * Modern distribution line insulator. Acts as wire relay.
 */
public class BlockHendrixVTI3Core extends BlockInsulatorBase
{
    public BlockHendrixVTI3Core()
    {
        super("hendrix_vti_3core");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityHendrixVTI3Core();
    }
}
