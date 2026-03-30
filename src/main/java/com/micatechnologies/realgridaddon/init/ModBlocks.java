package com.micatechnologies.realgridaddon.init;

import com.micatechnologies.realgridaddon.RealGridAddon;
import com.micatechnologies.realgridaddon.blocks.transformers.BlockClassATransformer2Wire;
import com.micatechnologies.realgridaddon.blocks.transformers.BlockClassATransformer1Wire;
import com.micatechnologies.realgridaddon.blocks.transformers.BlockClassCTransformer2Wire;
import com.micatechnologies.realgridaddon.blocks.transformers.BlockClassCTransformer1Wire;
import com.micatechnologies.realgridaddon.blocks.insulators.BlockMacLeanFNeckInsulator;
import com.micatechnologies.realgridaddon.blocks.insulators.BlockMacLeanDeadEndInsulator;
import com.micatechnologies.realgridaddon.blocks.insulators.BlockHendrixViseTopInsulator;
import com.micatechnologies.realgridaddon.blocks.insulators.BlockPorcelainPostTopInsulator;
import com.micatechnologies.realgridaddon.blocks.insulators.BlockPorcelainDeadEndInsulator;
import com.micatechnologies.realgridaddon.blocks.switchgear.BlockDistributionSwitch;
import com.micatechnologies.realgridaddon.items.ItemBlockBase;
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

@Mod.EventBusSubscriber(modid = RealGridAddon.MODID)
public class ModBlocks
{
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<ItemBlock> ITEM_BLOCKS = new ArrayList<>();

    // Transformers
    public static final BlockClassATransformer2Wire CLASS_A_TRANSFORMER_2WIRE = new BlockClassATransformer2Wire();
    public static final BlockClassATransformer1Wire CLASS_A_TRANSFORMER_1WIRE = new BlockClassATransformer1Wire();
    public static final BlockClassCTransformer2Wire CLASS_C_TRANSFORMER_2WIRE = new BlockClassCTransformer2Wire();
    public static final BlockClassCTransformer1Wire CLASS_C_TRANSFORMER_1WIRE = new BlockClassCTransformer1Wire();

    // Insulators
    public static final BlockMacLeanFNeckInsulator MACLEAN_FNECK_INSULATOR = new BlockMacLeanFNeckInsulator();
    public static final BlockMacLeanDeadEndInsulator MACLEAN_DEAD_END_INSULATOR = new BlockMacLeanDeadEndInsulator();
    public static final BlockHendrixViseTopInsulator HENDRIX_VISE_TOP_INSULATOR = new BlockHendrixViseTopInsulator();
    public static final BlockPorcelainPostTopInsulator PORCELAIN_POST_TOP_INSULATOR = new BlockPorcelainPostTopInsulator();
    public static final BlockPorcelainDeadEndInsulator PORCELAIN_DEAD_END_INSULATOR = new BlockPorcelainDeadEndInsulator();

    // Switchgear
    public static final BlockDistributionSwitch DISTRIBUTION_SWITCH = new BlockDistributionSwitch();

    // Creative Tab
    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(RealGridAddon.MODID)
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
            MACLEAN_FNECK_INSULATOR,
            MACLEAN_DEAD_END_INSULATOR,
            HENDRIX_VISE_TOP_INSULATOR,
            PORCELAIN_POST_TOP_INSULATOR,
            PORCELAIN_DEAD_END_INSULATOR,
            DISTRIBUTION_SWITCH
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
