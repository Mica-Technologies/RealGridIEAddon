package com.micatechnologies.realgrid.init;

import net.minecraft.tileentity.TileEntity;

/**
 * Interface for blocks that provide a tile entity. Used by
 * {@link ModTileEntities} to auto-discover and register tile entities
 * from the block registry, rather than maintaining a separate manual list.
 *
 * <p>Modelled after CSM's {@code ICsmTileEntityProvider}.
 */
public interface IRealGridTileEntityProvider
{
    /**
     * @return the tile entity class to register with Forge
     */
    Class<? extends TileEntity> getTileEntityClass();

    /**
     * @return the unique registration name for this tile entity (without
     *         namespace prefix). Multiple blocks may return the same name
     *         if they share a tile entity class -- only one registration
     *         will occur.
     */
    String getTileEntityName();
}
