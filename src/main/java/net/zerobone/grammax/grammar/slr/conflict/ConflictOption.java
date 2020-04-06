package net.zerobone.grammax.grammar.slr.conflict;

import net.zerobone.grammax.grammar.slr.Automation;

public abstract class ConflictOption {

    protected static final int SHORT_NAME_SHIFT = 0;

    protected static final int SHORT_NAME_REDUCE = 1;

    protected static final int SHORT_NAME_ACCEPT = 2;

    private static final String[] shortNames = {"shift", "reduce", "accept"};

    private final int shortName;

    protected ConflictOption(int shortName) {

        assert shortName == SHORT_NAME_SHIFT ||
            shortName == SHORT_NAME_REDUCE ||
            shortName == SHORT_NAME_ACCEPT;

        this.shortName = shortName;

    }

    public abstract String toString(Automation automation);

}