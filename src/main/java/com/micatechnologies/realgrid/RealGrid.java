package com.micatechnologies.realgrid;

import com.micatechnologies.realgrid.Tags;
import com.micatechnologies.realgrid.proxy.CommonProxy;
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
public class RealGrid
{
    public static final String MODID = Tags.MODID;
    public static final String NAME = Tags.MODNAME;
    public static final String VERSION = Tags.VERSION;

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static RealGrid instance;

    @SidedProxy(clientSide = "com.micatechnologies.realgrid.proxy.ClientProxy",
                serverSide = "com.micatechnologies.realgrid.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER.info("RealGrid Pre-Initialization");
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        LOGGER.info("RealGrid Initialization");
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        LOGGER.info("RealGrid Post-Initialization");
        proxy.postInit(event);
    }
}
