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

@Mod.EventBusSubscriber(modid = RealGrid.MODID, value = Side.CLIENT)
public class ModItems
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        for (Item item : RealGridRegistry.getItems())
        {
            ModelLoader.setCustomModelResourceLocation(
                item, 0,
                new ModelResourceLocation(item.getRegistryName(), "inventory")
            );
            if (item instanceof ItemBlock
                && ((ItemBlock) item).getBlock() instanceof BlockColoredInsulatorBase)
            {
                ModelLoader.setCustomModelResourceLocation(
                    item, 1,
                    new ModelResourceLocation(item.getRegistryName(), "inventory")
                );
            }
        }
    }
}
