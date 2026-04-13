package com.micatechnologies.realgrid.init;

import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.blocks.insulators.*;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassATransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassATransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassCTransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassCTransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockJumboTransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockJumboTransformer2Wire;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch2;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch3;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch4;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch5;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch6;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Block declarations and Forge registry event handlers.
 *
 * <p>Each block self-registers with {@link RealGridRegistry} in its base class
 * constructor, so instantiating the static fields below is all that is needed
 * to populate the registry. The Forge event handlers simply dump the registry
 * contents into Forge's own registries.
 */
@Mod.EventBusSubscriber(modid = RealGrid.MODID)
public class ModBlocks
{
    // Transformers
    public static final BlockClassATransformer2Wire CLASS_A_TRANSFORMER_2WIRE = new BlockClassATransformer2Wire();
    public static final BlockClassATransformer1Wire CLASS_A_TRANSFORMER_1WIRE = new BlockClassATransformer1Wire();
    public static final BlockClassCTransformer2Wire CLASS_C_TRANSFORMER_2WIRE = new BlockClassCTransformer2Wire();
    public static final BlockClassCTransformer1Wire CLASS_C_TRANSFORMER_1WIRE = new BlockClassCTransformer1Wire();
    public static final BlockJumboTransformer2Wire JUMBO_TRANSFORMER_2WIRE = new BlockJumboTransformer2Wire();
    public static final BlockJumboTransformer1Wire JUMBO_TRANSFORMER_1WIRE = new BlockJumboTransformer1Wire();

    // Insulators
    public static final BlockHendrixVTI2CoreSide HENDRIX_VTI_2CORE_SIDE = new BlockHendrixVTI2CoreSide();
    public static final BlockHendrixVTI3CoreSide HENDRIX_VTI_3CORE_SIDE = new BlockHendrixVTI3CoreSide();
    public static final BlockHendrixVTI3CoreSideSmall HENDRIX_VTI_3CORE_SIDE_SMALL = new BlockHendrixVTI3CoreSideSmall();
    public static final BlockHendrixVTI2Core HENDRIX_VTI_2CORE = new BlockHendrixVTI2Core();
    public static final BlockHendrixVTI3Core HENDRIX_VTI_3CORE = new BlockHendrixVTI3Core();
    public static final BlockHendrixVTI3CoreSmall HENDRIX_VTI_3CORE_SMALL = new BlockHendrixVTI3CoreSmall();
    public static final BlockLocke2CINew LOCKE_2CI_NEW = new BlockLocke2CINew();
    public static final BlockLocke2CIOld LOCKE_2CI_OLD = new BlockLocke2CIOld();
    public static final BlockLocke3CINew LOCKE_3CI_NEW = new BlockLocke3CINew();
    public static final BlockLocke3CIOld LOCKE_3CI_OLD = new BlockLocke3CIOld();
    public static final BlockMacLeanDEI1BellNew MACLEAN_DEI_1BELL_NEW = new BlockMacLeanDEI1BellNew();
    public static final BlockMacLeanDEI1BellOld MACLEAN_DEI_1BELL_OLD = new BlockMacLeanDEI1BellOld();
    public static final BlockMacLeanDEI2BellNew MACLEAN_DEI_2BELL_NEW = new BlockMacLeanDEI2BellNew();
    public static final BlockMacLeanDEI2BellOld MACLEAN_DEI_2BELL_OLD = new BlockMacLeanDEI2BellOld();
    public static final BlockMacLeanDEI3BellNew MACLEAN_DEI_3BELL_NEW = new BlockMacLeanDEI3BellNew();
    public static final BlockMacLeanDEI3BellOld MACLEAN_DEI_3BELL_OLD = new BlockMacLeanDEI3BellOld();
    public static final BlockMacLeanDEI3Core MACLEAN_DEI_3CORE = new BlockMacLeanDEI3Core();
    public static final BlockMacLeanDEI4Core MACLEAN_DEI_4CORE = new BlockMacLeanDEI4Core();
    public static final BlockMacLeanDEI4Core2 MACLEAN_DEI_4CORE_2 = new BlockMacLeanDEI4Core2();
    public static final BlockMacLeanDEI6Core MACLEAN_DEI_6CORE = new BlockMacLeanDEI6Core();
    public static final BlockMacLeanDEI7Core MACLEAN_DEI_7CORE = new BlockMacLeanDEI7Core();
    public static final BlockMacLeanDEI7Core2 MACLEAN_DEI_7CORE_2 = new BlockMacLeanDEI7Core2();
    public static final BlockMacLeanDEI7Core3 MACLEAN_DEI_7CORE_3 = new BlockMacLeanDEI7Core3();
    public static final BlockMacLeanDEI8Core MACLEAN_DEI_8CORE = new BlockMacLeanDEI8Core();
    public static final BlockMacLeanDEI9Core MACLEAN_DEI_9CORE = new BlockMacLeanDEI9Core();
    public static final BlockMacLeanPINew MACLEAN_PI_NEW = new BlockMacLeanPINew();
    public static final BlockMacLeanPINewSmall MACLEAN_PI_NEW_SMALL = new BlockMacLeanPINewSmall();
    public static final BlockMacLeanPINewSmall2 MACLEAN_PI_NEW_SMALL_2 = new BlockMacLeanPINewSmall2();
    public static final BlockMacLeanPIOld MACLEAN_PI_OLD = new BlockMacLeanPIOld();
    public static final BlockMacLeanPIOldSmall MACLEAN_PI_OLD_SMALL = new BlockMacLeanPIOldSmall();
    public static final BlockMacLeanPINewSide MACLEAN_PI_NEW_SIDE = new BlockMacLeanPINewSide();
    public static final BlockMacLeanPINewSideSmall MACLEAN_PI_NEW_SIDE_SMALL = new BlockMacLeanPINewSideSmall();
    public static final BlockMacLeanPIOldSide MACLEAN_PI_OLD_SIDE = new BlockMacLeanPIOldSide();
    public static final BlockMacLeanPIOldSideSmall MACLEAN_PI_OLD_SIDE_SMALL = new BlockMacLeanPIOldSideSmall();
    public static final BlockMacLeanPTI2Core MACLEAN_PTI_2CORE = new BlockMacLeanPTI2Core();
    public static final BlockMacLeanPTI3Core MACLEAN_PTI_3CORE = new BlockMacLeanPTI3Core();
    public static final BlockMacLeanPTI5Core MACLEAN_PTI_5CORE = new BlockMacLeanPTI5Core();
    public static final BlockMacLeanPTI5Core2 MACLEAN_PTI_5CORE_2 = new BlockMacLeanPTI5Core2();
    public static final BlockMacLeanPTI5Core3 MACLEAN_PTI_5CORE_3 = new BlockMacLeanPTI5Core3();
    public static final BlockMacLeanPTI6Core MACLEAN_PTI_6CORE = new BlockMacLeanPTI6Core();
    public static final BlockMacLeanPTI2CoreSide MACLEAN_PTI_2CORE_SIDE = new BlockMacLeanPTI2CoreSide();
    public static final BlockMacLeanPTI3CoreSide MACLEAN_PTI_3CORE_SIDE = new BlockMacLeanPTI3CoreSide();
    public static final BlockMacLeanPTI6CoreSide MACLEAN_PTI_6CORE_SIDE = new BlockMacLeanPTI6CoreSide();
    public static final BlockLocke2CINewSide LOCKE_2CI_NEW_SIDE = new BlockLocke2CINewSide();
    public static final BlockLocke2CIOldSide LOCKE_2CI_OLD_SIDE = new BlockLocke2CIOldSide();
    public static final BlockLocke3CINewSide LOCKE_3CI_NEW_SIDE = new BlockLocke3CINewSide();
    public static final BlockLocke3CIOldSide LOCKE_3CI_OLD_SIDE = new BlockLocke3CIOldSide();
    public static final BlockWireGuideInsulator WIRE_GUIDE_INSULATOR = new BlockWireGuideInsulator();
    public static final BlockWireGuideInsulator2 WIRE_GUIDE_INSULATOR_2 = new BlockWireGuideInsulator2();

    // Cutoffs
    public static final BlockCutoffSwitch CUTOFF_SWITCH = new BlockCutoffSwitch();
    public static final BlockCutoffSwitch2 CUTOFF_SWITCH_2 = new BlockCutoffSwitch2();
    public static final BlockCutoffSwitch3 CUTOFF_SWITCH_3 = new BlockCutoffSwitch3();
    public static final BlockCutoffSwitch4 CUTOFF_SWITCH_4 = new BlockCutoffSwitch4();
    public static final BlockCutoffSwitch5 CUTOFF_SWITCH_5 = new BlockCutoffSwitch5();
    public static final BlockCutoffSwitch6 CUTOFF_SWITCH_6 = new BlockCutoffSwitch6();

    // Creative Tab
    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(RealGrid.MODID) {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(CLASS_A_TRANSFORMER_2WIRE);
        }
    };

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        for (Block block : RealGridRegistry.getBlocks())
        {
            block.setCreativeTab(CREATIVE_TAB);
            event.getRegistry().register(block);
        }
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
            RealGridRegistry.getItems().toArray(new Item[0]));
    }
}
