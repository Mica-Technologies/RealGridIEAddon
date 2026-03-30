package com.micatechnologies.realgridaddon;

import com.micatechnologies.realgridaddon.Tags;
import com.micatechnologies.realgridaddon.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MODID,
     name = Tags.MODNAME,
     version = Tags.VERSION,
     dependencies = "required-after:immersiveengineering")
public class RealGridAddon
{
    public static final String MODID = Tags.MODID;
    public static final String NAME = Tags.MODNAME;
    public static final String VERSION = Tags.VERSION;

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static RealGridAddon instance;

    @SidedProxy(clientSide = "com.micatechnologies.realgridaddon.proxy.ClientProxy",
                serverSide = "com.micatechnologies.realgridaddon.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER.info("Real Grid Addon Pre-Initialization");
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        LOGGER.info("Real Grid Addon Initialization");
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        LOGGER.info("Real Grid Addon Post-Initialization");
        proxy.postInit(event);
    }
}
