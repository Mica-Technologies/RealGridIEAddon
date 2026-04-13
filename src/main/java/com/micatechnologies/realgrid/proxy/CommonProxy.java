package com.micatechnologies.realgrid.proxy;

import com.micatechnologies.realgrid.init.ModBlocks;
import com.micatechnologies.realgrid.init.ModTileEntities;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        // Force ModBlocks class initialization so all block constructors run
        // and populate RealGridRegistry before TE registration iterates it.
        // This is necessary because @Mod.EventBusSubscriber may not trigger
        // class initialization before preInit.
        ModBlocks.ensureLoaded();
        ModTileEntities.register();
    }

    public void init(FMLInitializationEvent event)
    {
    }

    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
