package com.micatechnologies.realgrid.blocks.insulators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * MacLean Dead End Insulator Block.
 * For terminating wire runs on poles. Acts as wire relay.
 */
public class BlockWireGuideInsulator2 extends BlockInsulatorBase
{
    public BlockWireGuideInsulator2()
    {
        super("wire_guide_insulator_2");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityWireGuideInsulator2();
    }
}
