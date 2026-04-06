package com.micatechnologies.realgrid.blocks.cutoffs;

/**
 * Cutoff Switch variant 3 — maclean_cutoff_switch_closed_3.
 *
 * All shared logic lives in {@link BlockCutoffSwitchBase}.
 */
public class BlockCutoffSwitch3 extends BlockCutoffSwitchBase {

    public BlockCutoffSwitch3() {
        super("maclean_cutoff_switch_closed_3");
    }

    @Override
    protected TileEntityCutoffSwitch createSwitchTE() {
        return new TileEntityCutoffSwitch3();
    }
}
