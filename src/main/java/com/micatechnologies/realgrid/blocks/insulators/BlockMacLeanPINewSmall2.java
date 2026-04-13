package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Porcelain Post Top Insulator Block (New, Small, Version 2).
 */
public class BlockMacLeanPINewSmall2 extends BlockInsulatorBase {

    public BlockMacLeanPINewSmall2() {
        super("small_maclean_porcelain_insulator_new_2");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityMacLeanPINewSmall2();
    }
}
