package net.zerobone.grammax.grammar.slr.conflict;

import net.zerobone.grammax.grammar.slr.Automation;

public final class Conflict {

    private final ConflictOption firstOption;

    private final ConflictOption secondOption;

    private final int state;

    public Conflict(ConflictOption firstOption, ConflictOption secondOption, int state) {
        this.firstOption = firstOption;
        this.secondOption = secondOption;
        this.state = state;
    }

    public String toString(Automation automation) {

        return firstOption.getShortName() + "/" +
            secondOption.getShortName() +
            ": should the parser " +
            firstOption.toString(automation) + " or " +
            secondOption.toString(automation) + " in the following state:\n" +
            automation.getParsingStateDescription(state);

    }

}