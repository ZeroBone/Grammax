package net.zerobone.grammax.grammar.slr.conflict;

import net.zerobone.grammax.grammar.slr.Automation;

public class ShiftReduceConflict extends Conflict {

    public final int reduceProductionId;

    public final int shiftTerminal;

    public ShiftReduceConflict(int reduceProductionId, int shiftTerminal) {
        this.reduceProductionId = reduceProductionId;
        this.shiftTerminal = shiftTerminal;
    }

    @Override
    public String toString(Automation automation) {
        return "shift/reduce conflict: Cannot decide whether to reduce '" +
            automation.productions[reduceProductionId].toString(automation) +
            "' or to shift '" + automation.terminalToSymbol(shiftTerminal) + "'";
    }

}