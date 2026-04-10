package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * MacLean F-Neck Distribution Line Post Insulator Block.
 * Sits on top of posts/poles. Acts as wire relay.
 */
public class BlockMacLeanPTI3Core extends BlockInsulatorBase
{
    public BlockMacLeanPTI3Core()
    {
        super("maclean_pti_3core");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityMacLeanPTI3Core();
    }
}
