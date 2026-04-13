package com.micatechnologies.realgrid.init;

import com.micatechnologies.realgrid.items.ItemBlockBase;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Centralized block and item registry for RealGrid.
 *
 * <p>Blocks self-register by calling {@link #registerBlock(Block)} in their
 * constructor. An {@link ItemBlockBase} is automatically created and registered
 * alongside each block. Forge registry events simply dump the collected
 * entries via {@link #getBlocks()} and {@link #getItems()}.
 *
 * <p>Modelled after CSM's {@code CsmRegistry} pattern: duplicate registration
 * is detected at construction time and throws immediately, rather than relying
 * on guard flags in event handlers.
 */
public class RealGridRegistry
{
    private static final Logger LOGGER = LogManager.getLogger(RealGridRegistry.class);

    private static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    private static final Map<String, Item> ITEMS = new LinkedHashMap<>();

    /**
     * Registers a block and auto-creates its {@link ItemBlockBase} wrapper.
     *
     * @throws IllegalArgumentException if a block with the same registry name
     *                                  is already registered
     */
    public static void registerBlock(Block block)
    {
        String key = Objects.requireNonNull(block.getRegistryName(),
            "Block must have a registry name before calling registerBlock()").toString();

        if (BLOCKS.containsKey(key))
        {
            throw new IllegalArgumentException(
                "Block with registry name " + key + " already registered.");
        }

        BLOCKS.put(key, block);

        // Auto-create and register the ItemBlock wrapper.
        ItemBlock itemBlock = new ItemBlockBase(block);
        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        registerItem(itemBlock);

        LOGGER.debug("Registered block: {}", key);
    }

    /**
     * Registers an item.
     *
     * @throws IllegalArgumentException if an item with the same registry name
     *                                  is already registered
     */
    public static void registerItem(Item item)
    {
        String key = Objects.requireNonNull(item.getRegistryName(),
            "Item must have a registry name before calling registerItem()").toString();

        if (ITEMS.containsKey(key))
        {
            throw new IllegalArgumentException(
                "Item with registry name " + key + " already registered.");
        }

        ITEMS.put(key, item);
    }

    /** Returns all registered blocks in insertion order. */
    public static Collection<Block> getBlocks()
    {
        return BLOCKS.values();
    }

    /** Returns all registered items in insertion order. */
    public static Collection<Item> getItems()
    {
        return ITEMS.values();
    }
}
