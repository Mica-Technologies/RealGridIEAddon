package com.micatechnologies.realgridaddon.init;

import com.micatechnologies.realgridaddon.RealGridAddon;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = RealGridAddon.MODID, value = Side.CLIENT)
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
