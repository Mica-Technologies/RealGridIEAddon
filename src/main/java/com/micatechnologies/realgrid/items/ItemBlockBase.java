package com.micatechnologies.realgrid.items;

import com.micatechnologies.realgrid.blocks.insulators.BlockColoredInsulatorBase;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockBase extends ItemBlock
{
    public ItemBlockBase(Block block)
    {
        super(block);
        if (block instanceof BlockColoredInsulatorBase) {
            setHasSubtypes(true);
            setMaxDamage(0);
        }
    }

    @Override
    public int getMetadata(int damage)
    {
        return getHasSubtypes() ? damage : 0;
    }
}
