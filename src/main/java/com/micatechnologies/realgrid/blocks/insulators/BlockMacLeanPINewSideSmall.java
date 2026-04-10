package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * MacLean Dead End Insulator Block.
 * For terminating wire runs on poles. Acts as wire relay.
 */
public class BlockMacLeanPINewSideSmall extends BlockInsulatorBase
{
    public BlockMacLeanPINewSideSmall()
    {
        super("small_maclean_porcelain_insulator_new_side");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityMacLeanPINewSideSmall();
    }
}
