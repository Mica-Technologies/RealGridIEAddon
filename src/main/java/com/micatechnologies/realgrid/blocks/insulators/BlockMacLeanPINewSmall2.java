package com.micatechnologies.realgrid.blocks.insulators;

/**
 * Porcelain Post Top Insulator Block — color variants: white (0) and black (1).
 *
 * All color-variant block state logic lives in {@link BlockColoredInsulatorBase}.
 */
public class BlockMacLeanPINewSmall2 extends BlockColoredInsulatorBase {

    public BlockMacLeanPINewSmall2() {
        super("small_maclean_porcelain_insulator_new_2");
    }

    @Override
    protected TileEntityColoredInsulatorBase createColoredTE() {
        return new TileEntityMacLeanPINewSmall2();
    }
}
