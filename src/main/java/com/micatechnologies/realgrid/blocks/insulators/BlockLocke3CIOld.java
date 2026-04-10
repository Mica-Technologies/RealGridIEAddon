package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * MacLean F-Neck Distribution Line Post Insulator Block.
 * Sits on top of posts/poles. Acts as wire relay.
 */
public class BlockLocke3CIOld extends BlockInsulatorBase
{
    public BlockLocke3CIOld()
    {
        super("locke_insulator_3core_old");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityLocke3CIOld();
    }
}
