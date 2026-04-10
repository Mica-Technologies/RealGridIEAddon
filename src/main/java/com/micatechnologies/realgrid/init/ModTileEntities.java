package com.micatechnologies.realgrid.init;

import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassATransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassATransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassCTransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityClassCTransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityJumboTransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.TileEntityJumboTransformer1Wire;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityHendrixVTI2CoreSide;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityHendrixVTI3CoreSide;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityHendrixVTI3CoreSideSmall;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityHendrixVTI2Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityHendrixVTI3Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityHendrixVTI3CoreSmall;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityLocke2CINew;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityLocke2CIOld;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityLocke3CINew;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityLocke3CIOld;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI1BellNew;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI1BellOld;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI2BellNew;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI2BellOld;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI3Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI4Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI4Core2;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI6Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI7Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI7Core2;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI7Core3;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI8Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanDEI9Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPINew;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPINewSmall;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPIOld;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPIOldSmall;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPINewSide;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPINewSideSmall;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPIOldSide;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPIOldSideSmall;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPTI2Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPTI3Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPTI5Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPTI5Core2;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPTI5Core3;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPTI6Core;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPTI2CoreSide;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPTI3CoreSide;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityMacLeanPTI6CoreSide;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityLocke2CINewSide;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityLocke2CIOldSide;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityLocke3CINewSide;
import com.micatechnologies.realgrid.blocks.insulators.TileEntityLocke3CIOldSide;
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
        GameRegistry.registerTileEntity(TileEntityLocke2CINew.class, 
            new ResourceLocation(RealGrid.MODID, "LOCKE_2CI_OLD"));
        GameRegistry.registerTileEntity(TileEntityLocke2CIOld.class, 
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
        GameRegistry.registerTileEntity(TileEntityLocke3CIOldSide.class, 
            new ResourceLocation(RealGrid.MODID, "LOCKE_3CI_OLD_SIDE"));
        GameRegistry.registerTileEntity(TileEntityCutoffSwitch.class,
            new ResourceLocation(RealGrid.MODID, "cutoff_switch"));
    }
}
