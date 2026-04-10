package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * MacLean Dead End Insulator Block.
 * For terminating wire runs on poles. Acts as wire relay.
 */
public class BlockMacLeanPTI6CoreSide extends BlockInsulatorBase
{
    public BlockMacLeanPTI6CoreSide()
    {
        super("maclean_pti_6core_side");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityMacLeanPTI6CoreSide();
    }
}
