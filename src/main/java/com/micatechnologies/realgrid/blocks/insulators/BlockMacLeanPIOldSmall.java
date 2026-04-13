package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Porcelain Post Top Insulator Block (Old, Small).
 */
public class BlockMacLeanPIOldSmall extends BlockInsulatorBase {

    public BlockMacLeanPIOldSmall() {
        super("small_maclean_porcelain_insulator_old");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityMacLeanPIOldSmall();
    }
}
