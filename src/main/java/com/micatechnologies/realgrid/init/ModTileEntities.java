package com.micatechnologies.realgrid.init;

import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.blocks.insulators.*;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassATransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassATransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassCTransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassCTransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityJumboTransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityJumboTransformer1Wire;
import com.micatechnologies.realgrid.blocks.cutoffs.TileEntityCutoffSwitch;
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
        GameRegistry.registerTileEntity(TileEntityHendrixVTI2CoreSide.class, 
            new ResourceLocation(RealGrid.MODID, "HENDRIX_VTI_2CORE_SIDE"));
        GameRegistry.registerTileEntity(TileEntityHendrixVTI3CoreSide.class, 
            new ResourceLocation(RealGrid.MODID, "HENDRIX_VTI_3CORE_SIDE"));
        GameRegistry.registerTileEntity(TileEntityHendrixVTI3CoreSideSmall.class, 
            new ResourceLocation(RealGrid.MODID, "HENDRIX_VTI_3CORE_SIDE_SMALL"));
        GameRegistry.registerTileEntity(TileEntityHendrixVTI2Core.class, 
            new ResourceLocation(RealGrid.MODID, "HENDRIX_VTI_2CORE"));
        GameRegistry.registerTileEntity(TileEntityHendrixVTI3Core.class, 
            new ResourceLocation(RealGrid.MODID, "HENDRIX_VTI_3CORE"));
        GameRegistry.registerTileEntity(TileEntityHendrixVTI3CoreSmall.class, 
            new ResourceLocation(RealGrid.MODID, "HENDRIX_VTI_3CORE_SMALL"));
        GameRegistry.registerTileEntity(TileEntityLocke2CINew.class,
            new ResourceLocation(RealGrid.MODID, "LOCKE_2CI_NEW"));
        GameRegistry.registerTileEntity(TileEntityLocke2CIOld.class,
            new ResourceLocation(RealGrid.MODID, "LOCKE_2CI_OLD"));
        GameRegistry.registerTileEntity(TileEntityLocke3CINew.class,
            new ResourceLocation(RealGrid.MODID, "LOCKE_3CI_NEW"));
        GameRegistry.registerTileEntity(TileEntityLocke3CIOld.class,
            new ResourceLocation(RealGrid.MODID, "LOCKE_3CI_OLD"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI1BellNew.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_1BELL_NEW"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI1BellOld.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_1BELL_OLD"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI2BellNew.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_2BELL_NEW"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI2BellOld.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_2BELL_OLD"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI3BellNew.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_3BELL_NEW"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI3BellOld.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_3BELL_OLD"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI3Core.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_3CORE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI4Core.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_4CORE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI4Core2.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_4CORE_2"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI6Core.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_6CORE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI7Core.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_7CORE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI7Core2.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_7CORE_2"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI7Core3.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_7CORE_3"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI8Core.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_8CORE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanDEI9Core.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_DEI_9CORE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPINew.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PI_NEW"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPINewSmall.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PI_NEW_SMALL"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPINewSmall2.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PI_NEW_SMALL_2"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPIOld.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PI_OLD"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPIOldSmall.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PI_OLD_SMALL"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPINewSide.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PI_NEW_SIDE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPINewSideSmall.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PI_NEW_SIDE_SMALL"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPIOldSide.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PI_OLD_SIDE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPIOldSideSmall.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PI_OLD_SIDE_SMALL"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPTI2Core.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PTI_2CORE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPTI3Core.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PTI_3CORE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPTI5Core.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PTI_5CORE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPTI5Core2.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PTI_5CORE_2"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPTI5Core3.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PTI_5CORE_3"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPTI6Core.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PTI_6CORE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPTI2CoreSide.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PTI_2CORE_SIDE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPTI3CoreSide.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PTI_3CORE_SIDE"));
        GameRegistry.registerTileEntity(TileEntityMacLeanPTI6CoreSide.class, 
            new ResourceLocation(RealGrid.MODID, "MACLEAN_PTI_6CORE_SIDE"));
        GameRegistry.registerTileEntity(TileEntityLocke2CINewSide.class, 
            new ResourceLocation(RealGrid.MODID, "LOCKE_2CI_NEW_SIDE"));
        GameRegistry.registerTileEntity(TileEntityLocke2CIOldSide.class, 
            new ResourceLocation(RealGrid.MODID, "LOCKE_2CI_OLD_SIDE"));
        GameRegistry.registerTileEntity(TileEntityLocke3CINewSide.class, 
            new ResourceLocation(RealGrid.MODID, "LOCKE_3CI_NEW_SIDE"));
        GameRegistry.registerTileEntity(TileEntityWireGuideInsulator.class, 
            new ResourceLocation(RealGrid.MODID, "WIRE_GUIDE_INSULATOR"));
        GameRegistry.registerTileEntity(TileEntityWireGuideInsulator2.class, 
            new ResourceLocation(RealGrid.MODID, "WIRE_GUIDE_INSULATOR_2"));        
        GameRegistry.registerTileEntity(TileEntityCutoffSwitch.class,
            new ResourceLocation(RealGrid.MODID, "cutoff_switch"));
    }
}
