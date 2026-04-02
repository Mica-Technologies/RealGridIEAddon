package com.micatechnologies.realgrid.init;

import com.micatechnologies.realgrid.RealGrid;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

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
        }
    }
}
