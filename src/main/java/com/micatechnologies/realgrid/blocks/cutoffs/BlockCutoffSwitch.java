package com.micatechnologies.realgrid.blocks.cutoffs;

/**
 * Cutoff Switch variant 1 — maclean_cutoff_switch_closed.
 *
 * All shared logic lives in {@link BlockCutoffSwitchBase}.
 */
public class BlockCutoffSwitch extends BlockCutoffSwitchBase {

    public BlockCutoffSwitch() {
        super("maclean_cutoff_switch_closed");
    }

    @Override
    protected TileEntityCutoffSwitch createSwitchTE() {
        return new TileEntityCutoffSwitch();
    }
}
