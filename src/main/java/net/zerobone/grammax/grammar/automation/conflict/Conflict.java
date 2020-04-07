package net.zerobone.grammax.grammar.automation.conflict;

import net.zerobone.grammax.grammar.automation.Automation;

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
            " conflict - should the parser " +
            firstOption.toString(automation) + " or " +
            secondOption.toString(automation) + " in state " + state + "?\n" +
            automation.getParsingStateDescription(state);

    }

}