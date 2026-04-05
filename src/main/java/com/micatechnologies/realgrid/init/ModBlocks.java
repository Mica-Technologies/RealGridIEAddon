package com.micatechnologies.realgrid.init;

import com.micatechnologies.realgrid.RealGrid;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassATransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassATransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassCTransformer2Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockClassCTransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockJumboTransformer1Wire;
import com.micatechnologies.realgrid.blocks.transformers.BlockJumboTransformer2Wire;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanFNeckInsulator;
import com.micatechnologies.realgrid.blocks.insulators.BlockMacLeanDeadEndInsulator;
import com.micatechnologies.realgrid.blocks.insulators.BlockHendrixViseTopInsulator;
import com.micatechnologies.realgrid.blocks.insulators.BlockPorcelainPostTopInsulator;
import com.micatechnologies.realgrid.blocks.insulators.BlockPorcelainDeadEndInsulator;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch2;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch3;
import com.micatechnologies.realgrid.blocks.cutoffs.BlockCutoffSwitch4;
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
    public static final BlockMacLeanFNeckInsulator MACLEAN_FNECK_INSULATOR = new BlockMacLeanFNeckInsulator();
    public static final BlockMacLeanDeadEndInsulator MACLEAN_DEAD_END_INSULATOR = new BlockMacLeanDeadEndInsulator();
    public static final BlockHendrixViseTopInsulator HENDRIX_VISE_TOP_INSULATOR = new BlockHendrixViseTopInsulator();
    public static final BlockPorcelainPostTopInsulator PORCELAIN_POST_TOP_INSULATOR = new BlockPorcelainPostTopInsulator();
    public static final BlockPorcelainDeadEndInsulator PORCELAIN_DEAD_END_INSULATOR = new BlockPorcelainDeadEndInsulator();

    // Switchgear
    public static final BlockCutoffSwitch CUTOFF_SWITCH = new BlockCutoffSwitch();
public static final BlockCutoffSwitch2 CUTOFF_SWITCH_2 = new BlockCutoffSwitch2();
public static final BlockCutoffSwitch3 CUTOFF_SWITCH_3 = new BlockCutoffSwitch3();
public static final BlockCutoffSwitch4 CUTOFF_SWITCH_4 = new BlockCutoffSwitch4();

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
            MACLEAN_FNECK_INSULATOR,
            MACLEAN_DEAD_END_INSULATOR,
            HENDRIX_VISE_TOP_INSULATOR,
            PORCELAIN_POST_TOP_INSULATOR,
            PORCELAIN_DEAD_END_INSULATOR,
            CUTOFF_SWITCH,
            CUTOFF_SWITCH_2,
            CUTOFF_SWITCH_3,
            CUTOFF_SWITCH_4
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
