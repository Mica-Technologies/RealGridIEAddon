package com.micatechnologies.realgrid.blocks.cutoffs;

/**
 * Cutoff Switch variant 5 — maclean_cutoff_switch_closed_5.
 *
 * All shared logic lives in {@link BlockCutoffSwitchBase}.
 */
public class BlockCutoffSwitch5 extends BlockCutoffSwitchBase {

    public BlockCutoffSwitch5() {
        super("maclean_cutoff_switch_closed_5");
    }

    @Override
    protected TileEntityCutoffSwitch createSwitchTE() {
        return new TileEntityCutoffSwitch();
    }
}
