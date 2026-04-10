package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * MacLean F-Neck Distribution Line Post Insulator Block.
 * Sits on top of posts/poles. Acts as wire relay.
 */
public class BlockLocke2CIOld extends BlockInsulatorBase
{
    public BlockLocke2CIOld()
    {
        super("locke_insulator_2core_old");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityLocke2CIOld();
    }
}
