package com.micatechnologies.realgrid.init;

import com.micatechnologies.realgrid.RealGrid;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Auto-discovers and registers tile entities by iterating blocks in
 * {@link RealGridRegistry} that implement {@link IRealGridTileEntityProvider}.
 *
 * <p>Modelled after CSM's tile entity registration pattern. Blocks that share
 * a TE class return the same {@code getTileEntityName()}, and only the first
 * occurrence triggers a registration.
 */
public class ModTileEntities
{
    private static final Logger LOGGER = LogManager.getLogger(ModTileEntities.class);

    public static void register()
    {
        Set<String> registered = new HashSet<>();

        for (Block block : RealGridRegistry.getBlocks())
        {
            if (block instanceof IRealGridTileEntityProvider)
            {
                IRealGridTileEntityProvider provider = (IRealGridTileEntityProvider) block;
                String name = provider.getTileEntityName();

                if (!registered.contains(name))
                {
                    GameRegistry.registerTileEntity(
                        provider.getTileEntityClass(),
                        new ResourceLocation(RealGrid.MODID, name));
                    registered.add(name);
                    LOGGER.debug("Registered tile entity: {}:{}", RealGrid.MODID, name);
                }
            }
        }

        LOGGER.info("Registered {} tile entity type(s)", registered.size());
    }
}
