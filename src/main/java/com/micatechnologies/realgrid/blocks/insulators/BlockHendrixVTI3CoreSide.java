package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * MacLean Dead End Insulator Block.
 * For terminating wire runs on poles. Acts as wire relay.
 */
public class BlockHendrixVTI3CoreSide extends BlockInsulatorBase
{
    public BlockHendrixVTI3CoreSide()
    {
        super("hendrix_vti_3core_side");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityHendrixVTI3CoreSide();
    }
}
