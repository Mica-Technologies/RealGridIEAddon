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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// @Mod.EventBusSubscriber auto-subscribes ALL static @SubscribeEvent methods in
// this class to the Forge event bus. Do NOT also call
// MinecraftForge.EVENT_BUS.register(ModBlocks.class) anywhere else — that would
// create a second subscription and cause every handler to fire twice.
// The handlers below are guarded against duplicate invocations (blocksRegistered /
// itemBlocksRegistered), but @Mod.EventBusSubscriber alone is the correct pattern.
@Mod.EventBusSubscriber(modid = RealGrid.MODID)
public class ModBlocks {

    private static final Logger LOGGER = LogManager.getLogger(ModBlocks.class);

    // Guard flags — set true on the first registration pass. If either handler
    // is somehow invoked a second time (stray manual register() call in the main
    // mod class, etc.) a warning is logged and the method returns immediately,
    // preventing every block / item-block from being entered into the registry twice.
    // FIX: Initialised to false so the first legitimate call is allowed through.
    private static boolean blocksRegistered = false;
    private static boolean itemBlocksRegistered = false;

    // Exposed as unmodifiable views so that no outside code can append to them
    // after class-load (which would silently re-introduce duplicate entries).
    public static final List<Block> BLOCKS;
    public static final List<ItemBlock> ITEM_BLOCKS;

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

    // Static initializer — the JVM runs this exactly once when the class is first
    // loaded, so BLOCKS and ITEM_BLOCKS are populated only once regardless of how
    // many times the Forge registry events fire.
    //
    // The mutable working lists are wrapped in unmodifiable views before being
    // assigned to the public fields, so no external code can append to them after
    // this point and silently introduce duplicate entries.
    //
    // Each ItemBlockBase instance is created once here and stored permanently in
    // ITEM_BLOCKS. The same instance is later passed to event.getRegistry().register()
    // in registerItemBlocks(), so the object in the Item registry and the object in
    // ITEM_BLOCKS are always identical — ModItems.registerModels() will never map
    // models to unregistered "ghost" objects.
    static {
        List<Block> mutableBlocks = new ArrayList<>();
        mutableBlocks.addAll(Arrays.asList(
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
            MACLEAN_DEI_3BELL_NEW,
            MACLEAN_DEI_3BELL_OLD,
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
            WIRE_GUIDE_INSULATOR,
            WIRE_GUIDE_INSULATOR_2,
            CUTOFF_SWITCH,
            CUTOFF_SWITCH_2,
            CUTOFF_SWITCH_3,
            CUTOFF_SWITCH_4,
            CUTOFF_SWITCH_5,
            CUTOFF_SWITCH_6
        ));

        List<ItemBlock> mutableItemBlocks = new ArrayList<>();
        for (Block block : mutableBlocks) {
            ItemBlock itemBlock = new ItemBlockBase(block);
            itemBlock.setRegistryName(block.getRegistryName());
            mutableItemBlocks.add(itemBlock);
        }

        // Seal both lists. Any attempt to call BLOCKS.add() or ITEM_BLOCKS.add()
        // after this point will throw UnsupportedOperationException at runtime,
        // making the failure loud and obvious rather than a silent duplicate.
        BLOCKS = Collections.unmodifiableList(mutableBlocks);
        ITEM_BLOCKS = Collections.unmodifiableList(mutableItemBlocks);
    }

    // Creative Tab
    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(RealGrid.MODID) {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(CLASS_A_TRANSFORMER_2WIRE);
        }
    };

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        if (blocksRegistered) {
            LOGGER.warn("[RealGrid] registerBlocks() invoked more than once — skipping duplicate "
                + "registration. Ensure ModBlocks is not manually registered on the event bus "
                + "in addition to @Mod.EventBusSubscriber.");
            return;
        }
        blocksRegistered = true;

        for (Block block : BLOCKS) {
            block.setCreativeTab(CREATIVE_TAB);
            event.getRegistry().register(block);
        }
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        if (itemBlocksRegistered) {
            LOGGER.warn("[RealGrid] registerItemBlocks() invoked more than once — skipping duplicate "
                + "registration. Ensure ModBlocks is not manually registered on the event bus "
                + "in addition to @Mod.EventBusSubscriber.");
            return;
        }
        itemBlocksRegistered = true;

        for (ItemBlock itemBlock : ITEM_BLOCKS) {
            event.getRegistry().register(itemBlock);
        }
    }
}
