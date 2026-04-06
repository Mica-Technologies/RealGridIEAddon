package com.micatechnologies.realgrid.blocks.cutoffs;

/**
 * Cutoff Switch variant 2 — maclean_cutoff_switch_closed_2.
 *
 * All shared logic lives in {@link BlockCutoffSwitchBase}.
 */
public class BlockCutoffSwitch2 extends BlockCutoffSwitchBase {

    public BlockCutoffSwitch2() {
        super("maclean_cutoff_switch_closed_2");
    }

    @Override
    protected TileEntityCutoffSwitch createSwitchTE() {
        return new TileEntityCutoffSwitch2();
    }
}
