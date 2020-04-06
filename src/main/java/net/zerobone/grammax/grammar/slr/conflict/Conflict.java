package net.zerobone.grammax.grammar.slr.conflict;

import net.zerobone.grammax.grammar.slr.Automation;
import net.zerobone.grammax.grammar.utils.Point;

import java.util.HashSet;

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

        StringBuilder sb = new StringBuilder();

        HashSet<Point> conflictState = automation.getParsingState(state);

        for (Point point : conflictState) {

            

        }

        return firstOption.getShortName() + "/" +
            secondOption.getShortName() +
            ": should the parser " +
            firstOption.toString(automation) + " or " +
            secondOption.toString(automation) + " in state " + state + "?";

    }

}