package com.micatechnologies.realgrid.blocks.crossarms;

/**
 * What a crossarm is made of and how it looks. Each one is its own block,
 * {@code realgrid:crossarm_<name>}, sharing {@link BlockCrossarm}'s behaviour; its models come from
 * scripts/gen_crossarms.py.
 *
 * <p>Issue #38 lists the full set: Brooks cedar in brown, tan and orange; PUPI fiberglass in white and
 * dark brown; Shakespeare fiberglass in maroon; MacLean fiberglass in tan, white and dark brown. The
 * foundation ships the first of them, and the rest come with the texture work.
 */
public enum CrossarmMaterial
{
    BROOKS_BROWN("brooks_brown", false);

    private final String name;
    private final boolean fiberglass;

    CrossarmMaterial(String name, boolean fiberglass)
    {
        this.name = name;
        this.fiberglass = fiberglass;
    }

    /** @return the registry and model name, such as "brooks_brown" */
    public String getName()
    {
        return name;
    }

    /** @return true for fiberglass arms, false for wood */
    public boolean isFiberglass()
    {
        return fiberglass;
    }
}
