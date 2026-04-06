package com.micatechnologies.realgrid.blocks.insulators;

/**
 * Porcelain Post Top Insulator Block — color variants: white (0) and black (1).
 *
 * All color-variant block state logic lives in {@link BlockColoredInsulatorBase}.
 */
public class BlockPorcelainPostTopInsulator extends BlockColoredInsulatorBase {

    public BlockPorcelainPostTopInsulator() {
        super("porcelain_post_top_insulator");
    }

    @Override
    protected TileEntityColoredInsulatorBase createColoredTE() {
        return new TileEntityPorcelainPostTopInsulator();
    }
}
