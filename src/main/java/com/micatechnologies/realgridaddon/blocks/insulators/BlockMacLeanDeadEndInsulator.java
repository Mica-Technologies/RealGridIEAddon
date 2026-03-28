package com.micatechnologies.realgridaddon.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * MacLean Dead End Insulator Block.
 * For terminating wire runs on poles. Acts as wire relay.
 */
public class BlockMacLeanDeadEndInsulator extends BlockInsulatorBase
{
    public BlockMacLeanDeadEndInsulator()
    {
        super("maclean_dead_end_insulator");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityMacLeanDeadEndInsulator();
    }
}
