package com.micatechnologies.realgrid.blocks.insulators;

/**
 * Porcelain Post Top Insulator Block — color variants: white (0) and black (1).
 *
 * All color-variant block state logic lives in {@link BlockColoredInsulatorBase}.
 */
public class BlockMacLeanPINew extends BlockColoredInsulatorBase {

    public BlockMacLeanPINew() {
        super("large_maclean_porcelain_insulator_new");
    }

    @Override
    protected TileEntityColoredInsulatorBase createColoredTE() {
        return new TileEntityMacLeanPINew();
    }
}
