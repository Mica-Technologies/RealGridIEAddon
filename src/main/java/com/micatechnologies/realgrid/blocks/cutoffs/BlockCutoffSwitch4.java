package com.micatechnologies.realgrid.blocks.cutoffs;

/**
 * Cutoff Switch variant 4 — maclean_cutoff_switch_closed_4.
 *
 * All shared logic lives in {@link BlockCutoffSwitchBase}.
 */
public class BlockCutoffSwitch4 extends BlockCutoffSwitchBase {

    public BlockCutoffSwitch4() {
        super("maclean_cutoff_switch_closed_4");
    }

    @Override
    protected TileEntityCutoffSwitch createSwitchTE() {
        return new TileEntityCutoffSwitch4();
    }
}
