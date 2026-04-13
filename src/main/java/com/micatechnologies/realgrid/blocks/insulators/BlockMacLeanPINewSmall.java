package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Porcelain Post Top Insulator Block (New, Small).
 */
public class BlockMacLeanPINewSmall extends BlockInsulatorBase {

    public BlockMacLeanPINewSmall() {
        super("small_maclean_porcelain_insulator_new");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityMacLeanPINewSmall();
    }
}
