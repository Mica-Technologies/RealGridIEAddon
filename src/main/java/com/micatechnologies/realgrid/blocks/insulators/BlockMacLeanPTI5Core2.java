package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * MacLean F-Neck Distribution Line Post Insulator Block.
 * Sits on top of posts/poles. Acts as wire relay.
 */
public class BlockMacLeanPTI5Core2 extends BlockInsulatorBase
{
    public BlockMacLeanPTI5Core2()
    {
        super("maclean_pti_5core_2");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityMacLeanPTI5Core2();
    }
}
