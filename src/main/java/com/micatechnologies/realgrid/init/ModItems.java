package com.micatechnologies.realgrid.init;

import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.blocks.insulators.BlockColoredInsulatorBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

// FIX: Class body braces were missing, rendering the class syntactically invalid.
// Added the opening '{' after the class declaration and closing '}' at end of file.
// Also added the missing '{' and '}' for the registerModels method body and
// the for-loop body.
@Mod.EventBusSubscriber(modid = RealGrid.MODID, value = Side.CLIENT)
public class ModItems
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        for (ItemBlock itemBlock : ModBlocks.ITEM_BLOCKS)
        {
            ModelLoader.setCustomModelResourceLocation(
                itemBlock, 0,
                new ModelResourceLocation(itemBlock.getRegistryName(), "inventory")
            );
            if (itemBlock.getBlock() instanceof BlockColoredInsulatorBase)
            {
                ModelLoader.setCustomModelResourceLocation(
                    itemBlock, 1,
                    new ModelResourceLocation(itemBlock.getRegistryName(), "inventory")
                );
            }
        }
    }
}
