package com.micatechnologies.realgrid.blocks.insulators;

/**
 * Porcelain Dead End Insulator Block — color variants: white (0) and red (1).
 *
 * All color-variant block state logic lives in {@link BlockColoredInsulatorBase}.
 */
public class BlockPorcelainDeadEndInsulator extends BlockColoredInsulatorBase {

    public BlockPorcelainDeadEndInsulator() {
        super("porcelain_dead_end_insulator");
    }

    @Override
    protected TileEntityColoredInsulatorBase createColoredTE() {
        return new TileEntityPorcelainDeadEndInsulator();
    }
}
