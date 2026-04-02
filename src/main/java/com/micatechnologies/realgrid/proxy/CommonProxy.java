package com.micatechnologies.realgrid.proxy;

import com.micatechnologies.realgrid.init.ModTileEntities;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        ModTileEntities.register();
    }

    public void init(FMLInitializationEvent event)
    {
    }

    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
