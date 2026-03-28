package com.micatechnologies.realgridaddon.init;

import com.micatechnologies.realgridaddon.RealGridAddon;
import com.micatechnologies.realgridaddon.blocks.transformers.TileEntityClassATransformer2Wire;
import com.micatechnologies.realgridaddon.blocks.transformers.TileEntityClassATransformer1Wire;
import com.micatechnologies.realgridaddon.blocks.transformers.TileEntityClassCTransformer2Wire;
import com.micatechnologies.realgridaddon.blocks.transformers.TileEntityClassCTransformer1Wire;
import com.micatechnologies.realgridaddon.blocks.insulators.TileEntityMacLeanFNeckInsulator;
import com.micatechnologies.realgridaddon.blocks.insulators.TileEntityMacLeanDeadEndInsulator;
import com.micatechnologies.realgridaddon.blocks.insulators.TileEntityHendrixViseTopInsulator;
import com.micatechnologies.realgridaddon.blocks.insulators.TileEntityPorcelainPostTopInsulator;
import com.micatechnologies.realgridaddon.blocks.insulators.TileEntityPorcelainDeadEndInsulator;
import com.micatechnologies.realgridaddon.blocks.switchgear.TileEntityDistributionSwitch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities
{
    public static void register()
    {
        GameRegistry.registerTileEntity(TileEntityClassATransformer2Wire.class,
            new ResourceLocation(RealGridAddon.MODID, "class_a_transformer_2wire"));
        GameRegistry.registerTileEntity(TileEntityClassATransformer1Wire.class,
            new ResourceLocation(RealGridAddon.MODID, "class_a_transformer_1wire"));
        GameRegistry.registerTileEntity(TileEntityClassCTransformer2Wire.class,
            new ResourceLocation(RealGridAddon.MODID, "class_c_transformer_2wire"));
        GameRegistry.registerTileEntity(TileEntityClassCTransformer1Wire.class,
            new ResourceLocation(RealGridAddon.MODID, "class_c_transformer_1wire"));

        GameRegistry.registerTileEntity(TileEntityMacLeanFNeckInsulator.class,
            new ResourceLocation(RealGridAddon.MODID, "maclean_fneck_insulator"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDeadEndInsulator.class,
            new ResourceLocation(RealGridAddon.MODID, "maclean_dead_end_insulator"));
        GameRegistry.registerTileEntity(TileEntityHendrixViseTopInsulator.class,
            new ResourceLocation(RealGridAddon.MODID, "hendrix_vise_top_insulator"));
        GameRegistry.registerTileEntity(TileEntityPorcelainPostTopInsulator.class,
            new ResourceLocation(RealGridAddon.MODID, "porcelain_post_top_insulator"));
        GameRegistry.registerTileEntity(TileEntityPorcelainDeadEndInsulator.class,
            new ResourceLocation(RealGridAddon.MODID, "porcelain_dead_end_insulator"));

        GameRegistry.registerTileEntity(TileEntityDistributionSwitch.class,
            new ResourceLocation(RealGridAddon.MODID, "distribution_switch"));
    }
}
