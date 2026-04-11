package com.micatechnologies.realgrid.blocks.insulators;

/**
 * Porcelain Post Top Insulator Block — color variants: white (0) and black (1).
 *
 * All color-variant block state logic lives in {@link BlockColoredInsulatorBase}.
 */
public class BlockMacLeanPINewSmall extends BlockColoredInsulatorBase {

    public BlockMacLeanPINewSmall() {
        super("small_maclean_porcelain_insulator_new");
    }

    @Override
    protected TileEntityInsulatorBase createColoredTE() {
        return new TileEntityMacLeanPINewSmall();
    }
}
