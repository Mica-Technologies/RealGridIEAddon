package com.micatechnologies.realgrid.blocks.crossarms;

/**
 * What a crossarm is made of and how it looks. Each one is its own block,
 * {@code realgrid:crossarm_<name>}, sharing {@link BlockCrossarm}'s behaviour; its models come from
 * scripts/gen_crossarms.py.
 *
 * <p>Issue #38 lists the full set: Brooks cedar in brown, tan and orange; PUPI fiberglass in white and
 * dark brown; Shakespeare fiberglass in maroon; MacLean fiberglass in tan, white and dark brown. Wood
 * arms are through-bolted to the pole; fiberglass arms sit in their maker's braceless bracket, drawn by
 * the arm itself (the generator's MATERIALS table says which bracket each one has).
 */
public enum CrossarmMaterial
{
    BROOKS_BROWN("brooks_brown", false),
    BROOKS_TAN("brooks_tan", false),
    BROOKS_ORANGE("brooks_orange", false),
    PUPI_WHITE("pupi_white", true),
    PUPI_DARK_BROWN("pupi_dark_brown", true),
    SHAKESPEARE_MAROON("shakespeare_maroon", true),
    MACLEAN_TAN("maclean_tan", true),
    MACLEAN_WHITE("maclean_white", true),
    MACLEAN_DARK_BROWN("maclean_dark_brown", true);

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
