package com.micatechnologies.realgrid.init;

import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassATransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassATransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassCTransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassCTransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityJumboTransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityJumboTransformer1Wire;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanFNeckInsulator;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDeadEndInsulator;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityHendrixViseTopInsulator;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityPorcelainPostTopInsulator;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityPorcelainDeadEndInsulator;
import com.micatechnologies.realgrid.blocks.cutoffs.TileEntityCutoffSwitch;
import com.micatechnologies.realgrid.blocks.cutoffs.TileEntityCutoffSwitch2;
import com.micatechnologies.realgrid.blocks.cutoffs.TileEntityCutoffSwitch3;
import com.micatechnologies.realgrid.blocks.cutoffs.TileEntityCutoffSwitch4;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities
{
    public static void register()
    {
        GameRegistry.registerTileEntity(TileEntityClassATransformer2Wire.class,
            new ResourceLocation(RealGrid.MODID, "class_a_transformer_2wire"));
        GameRegistry.registerTileEntity(TileEntityClassATransformer1Wire.class,
            new ResourceLocation(RealGrid.MODID, "class_a_transformer_1wire"));
        GameRegistry.registerTileEntity(TileEntityClassCTransformer2Wire.class,
            new ResourceLocation(RealGrid.MODID, "class_c_transformer_2wire"));
        GameRegistry.registerTileEntity(TileEntityClassCTransformer1Wire.class,
            new ResourceLocation(RealGrid.MODID, "class_c_transformer_1wire"));
        GameRegistry.registerTileEntity(TileEntityJumboTransformer2Wire.class,
            new ResourceLocation(RealGrid.MODID, "jumbo_transformer_2wire"));
        GameRegistry.registerTileEntity(TileEntityJumboTransformer1Wire.class,
            new ResourceLocation(RealGrid.MODID, "jumbo_transformer_1wire"));

        GameRegistry.registerTileEntity(TileEntityMacLeanFNeckInsulator.class,
            new ResourceLocation(RealGrid.MODID, "maclean_fneck_insulator"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDeadEndInsulator.class,
            new ResourceLocation(RealGrid.MODID, "maclean_dead_end_insulator"));
        GameRegistry.registerTileEntity(TileEntityHendrixViseTopInsulator.class,
            new ResourceLocation(RealGrid.MODID, "hendrix_vise_top_insulator"));
        GameRegistry.registerTileEntity(TileEntityPorcelainPostTopInsulator.class,
            new ResourceLocation(RealGrid.MODID, "porcelain_post_top_insulator"));
        GameRegistry.registerTileEntity(TileEntityPorcelainDeadEndInsulator.class,
            new ResourceLocation(RealGrid.MODID, "porcelain_dead_end_insulator"));

        GameRegistry.registerTileEntity(TileEntityCutoffSwitch.class,
            new ResourceLocation(RealGrid.MODID, "cutoff_switch"));
        GameRegistry.registerTileEntity(TileEntityCutoffSwitch2.class,
            new ResourceLocation(RealGrid.MODID, "cutoff_switch_2"));
        GameRegistry.registerTileEntity(TileEntityCutoffSwitch3.class,
            new ResourceLocation(RealGrid.MODID, "cutoff_switch_3"));
        GameRegistry.registerTileEntity(TileEntityCutoffSwitch4.class,
            new ResourceLocation(RealGrid.MODID, "cutoff_switch_4"));
    }
}
