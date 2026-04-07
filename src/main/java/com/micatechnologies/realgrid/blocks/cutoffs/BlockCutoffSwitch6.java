package com.micatechnologies.realgrid.blocks.cutoffs;

/**
 * Cutoff Switch variant 6 — maclean_cutoff_switch_closed_6.
 *
 * All shared logic lives in {@link BlockCutoffSwitchBase}.
 */
public class BlockCutoffSwitch6 extends BlockCutoffSwitchBase {

    public BlockCutoffSwitch6() {
        super("maclean_cutoff_switch_closed_6");
    }

    @Override
    protected TileEntityCutoffSwitch createSwitchTE() {
        return new TileEntityCutoffSwitch();
    }
}
