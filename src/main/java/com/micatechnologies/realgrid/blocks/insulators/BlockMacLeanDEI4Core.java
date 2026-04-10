package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * MacLean Dead End Insulator Block.
 * For terminating wire runs on poles. Acts as wire relay.
 */
public class BlockMacLeanDEI4Core extends BlockInsulatorBase
{
    public BlockMacLeanDEI4Core()
    {
        super("maclean_fiberglass_dei_4core");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityMacLeanDEI4Core();
    }
}
