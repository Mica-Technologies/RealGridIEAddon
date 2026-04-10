package com.micatechnologies.realgrid.blocks.insulators;

/**
 * Porcelain Post Top Insulator Block — color variants: white (0) and black (1).
 *
 * All color-variant block state logic lives in {@link BlockColoredInsulatorBase}.
 */
public class BlockMacLeanPIOldSmall extends BlockColoredInsulatorBase {

    public BlockMacLeanPIOldSmall() {
        super("small_maclean_porcelain_insulator_old");
    }

    @Override
    protected TileEntityColoredInsulatorBase createColoredTE() {
        return new TileEntityMacLeanPIOldSmall();
    }
}
