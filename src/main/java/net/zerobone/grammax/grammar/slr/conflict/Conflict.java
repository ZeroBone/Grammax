package net.zerobone.grammax.grammar.slr.conflict;

import net.zerobone.grammax.grammar.slr.Automation;

public final class Conflict {

    private final ConflictOption firstOption;

    private final ConflictOption secondOption;

    public Conflict(ConflictOption firstOption, ConflictOption secondOption) {
        this.firstOption = firstOption;
        this.secondOption = secondOption;
    }

    public String toString(Automation automation) {

    }

}