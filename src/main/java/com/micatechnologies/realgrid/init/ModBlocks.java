package com.micatechnologies.realgrid.init;

import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassATransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassATransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassCTransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassCTransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockJumboTransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockJumboTransformer2Wire;
import com.micatechnologies.realgrid.blocks.insulators.BlockHendrixVTI2CoreSide;
import com.micatechnologies.realgrid.blocks.insulators.BlockHendrixVTI3CoreSide;
import com.micatechnologies.realgrid.blocks.insulators.BlockHendrixVTI3CoreSideSmall;
import com.micatechnologies.realgrid.blocks.insulators.BlockHendrixVTI2Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockHendrixVTI3Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockHendrixVTI3CoreSmall;
import com.micatechnologies.realgrid.blocks.insulators.BlockLocke2CINew;
import com.micatechnologies.realgrid.blocks.insulators.BlockLocke2CIOld;
import com.micatechnologies.realgrid.blocks.insulators.BlockLocke3CINew;
import com.micatechnologies.realgrid.blocks.insulators.BlockLocke3CIOld;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI1BellNew;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI1BellOld;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI2BellNew;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI2BellOld;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI3Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI4Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI4Core2;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI6Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI7Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI7Core2;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI7Core3;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI8Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDEI9Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPINew;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPINewSmall;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPIOld;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPIOldSmall;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPINewSide;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPINewSideSmall;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPIOldSide;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPIOldSideSmall;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPTI2Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPTI3Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPTI5Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPTI5Core2;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPTI5Core3;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPTI6Core;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPTI2CoreSide;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPTI3CoreSide;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanPTI6CoreSide;
import com.micatechnologies.realgrid.blocks.insulators.BlockLocke2CINewSide;
import com.micatechnologies.realgrid.blocks.insulators.BlockLocke2CIOldSide;
import com.micatechnologies.realgrid.blocks.insulators.BlockLocke3CINewSide;
import com.micatechnologies.realgrid.blocks.insulators.BlockLocke3CIOldSide;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch2;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch3;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch4;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch5;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch6;
import com.micatechnologies.realgrid.items.ItemBlockBase;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = RealGrid.MODID)
public class ModBlocks
{
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<ItemBlock> ITEM_BLOCKS = new ArrayList<>();

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
    public static final BlockLocke2CINew LOCKE_2CI_OLD = new BlockLocke2CIOld();
    public static final BlockLocke2CIOld LOCKE_3CI_NEW = new BlockLocke3CINew();
    public static final BlockLocke3CIOld LOCKE_3CI_OLD = new BlockLocke3CIOld();
    public static final BlockMacLeanDEI1BellNew MACLEAN_DEI_1BELL_NEW = new BlockMacLeanDEI1BellNew();
    public static final BlockMacLeanDEI1BellOld MACLEAN_DEI_1BELL_OLD = new BlockMacLeanDEI1BellOld();
    public static final BlockMacLeanDEI2BellNew MACLEAN_DEI_2BELL_NEW = new BlockMacLeanDEI2BellNew();
    public static final BlockMacLeanDEI2BellOld MACLEAN_DEI_2BELL_OLD = new BlockMacLeanDEI2BellOld();
    public static final BlockMacLeanDEI3Core MACLEAN_DEI_3CORE = new BlockMacLeanDEI3Core();
    public static final BlockMacLeanDEI4Core MACLEAN_DEI_4CORE = new BlockMacLeanDEI4Core();
    public static final BlockMacLeanDEI4Core2 MACLEAN_DEI_4CORE_2 = new  BlockMacLeanDEI4Core2();
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

    // Cutoffs
    public static final BlockCutoffSwitch CUTOFF_SWITCH = new BlockCutoffSwitch();
    public static final BlockCutoffSwitch2 CUTOFF_SWITCH_2 = new BlockCutoffSwitch2();
    public static final BlockCutoffSwitch3 CUTOFF_SWITCH_3 = new BlockCutoffSwitch3();
    public static final BlockCutoffSwitch4 CUTOFF_SWITCH_4 = new BlockCutoffSwitch4();
    public static final BlockCutoffSwitch5 CUTOFF_SWITCH_5 = new BlockCutoffSwitch5();
    public static final BlockCutoffSwitch6 CUTOFF_SWITCH_6 = new BlockCutoffSwitch6();

    // Creative Tab
    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(RealGrid.MODID)
    {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack createIcon()
        {
            return new ItemStack(CLASS_A_TRANSFORMER_2WIRE);
        }
    };

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        Block[] blocks = {
            CLASS_A_TRANSFORMER_2WIRE,
            CLASS_A_TRANSFORMER_1WIRE,
            CLASS_C_TRANSFORMER_2WIRE,
            CLASS_C_TRANSFORMER_1WIRE,
            JUMBO_TRANSFORMER_2WIRE,
            JUMBO_TRANSFORMER_1WIRE,
            HENDRIX_VTI_2CORE_SIDE,
            HENDRIX_VTI_3CORE_SIDE,
            HENDRIX_VTI_3CORE_SIDE_SMALL,
            HENDRIX_VTI_2CORE,
            HENDRIX_VTI_3CORE,
            HENDRIX_VTI_3CORE_SMALL,
            LOCKE_2CI_NEW,
            LOCKE_2CI_OLD,
            LOCKE_3CI_NEW,
            LOCKE_3CI_OLD,
            MACLEAN_DEI_1BELL_NEW,
            MACLEAN_DEI_1BELL_OLD,
            MACLEAN_DEI_2BELL_NEW,
            MACLEAN_DEI_2BELL_OLD,
            MACLEAN_DEI_3CORE,
            MACLEAN_DEI_4CORE,
            MACLEAN_DEI_4CORE_2,
            MACLEAN_DEI_6CORE,
            MACLEAN_DEI_7CORE,
            MACLEAN_DEI_7CORE_2,
            MACLEAN_DEI_7CORE_3,
            MACLEAN_DEI_8CORE,
            MACLEAN_DEI_9CORE,
            MACLEAN_PI_NEW,
            MACLEAN_PI_NEW_SMALL,
            MACLEAN_PI_NEW_SMALL_2,
            MACLEAN_PI_OLD,
            MACLEAN_PI_OLD_SMALL,
            MACLEAN_PI_NEW_SIDE,
            MACLEAN_PI_NEW_SIDE_SMALL,
            MACLEAN_PI_OLD_SIDE,
            MACLEAN_PI_OLD_SIDE_SMALL,
            MACLEAN_PTI_2CORE,
            MACLEAN_PTI_3CORE,
            MACLEAN_PTI_5CORE,
            MACLEAN_PTI_5CORE_2,
            MACLEAN_PTI_5CORE_3,
            MACLEAN_PTI_6CORE,
            MACLEAN_PTI_2CORE_SIDE,
            MACLEAN_PTI_3CORE_SIDE,
            MACLEAN_PTI_6CORE_SIDE,
            LOCKE_2CI_NEW_SIDE,
            LOCKE_2CI_OLD_SIDE,
            LOCKE_3CI_NEW_SIDE,
            LOCKE_3CI_OLD_SIDE,
            CUTOFF_SWITCH,
            CUTOFF_SWITCH_2,
            CUTOFF_SWITCH_3,
            CUTOFF_SWITCH_4,
            CUTOFF_SWITCH_5,
            CUTOFF_SWITCH_6
        };

        for (Block block : blocks)
        {
            block.setCreativeTab(CREATIVE_TAB);
            event.getRegistry().register(block);
            BLOCKS.add(block);
        }
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event)
    {
        for (Block block : BLOCKS)
        {
            ItemBlock itemBlock = new ItemBlockBase(block);
            itemBlock.setRegistryName(block.getRegistryName());
            event.getRegistry().register(itemBlock);
            ITEM_BLOCKS.add(itemBlock);
        }
    }
}
