package net.zerobone.grammax.grammar.slr.conflict;

import net.zerobone.grammax.grammar.slr.Automation;

public class ReduceReduceConflict extends Conflict {

    public final int firstProductionId;

    public final int secondProductionId;

    public ReduceReduceConflict(int firstProductionId, int secondProductionId) {
        this.firstProductionId = firstProductionId;
        this.secondProductionId = secondProductionId;
    }

    @Override
    public String toString(Automation automation) {
        return "reduce/reduce conflict: Cannot decide what to reduce - '" +
            automation.productions[firstProductionId].toString(automation) +
            "' or '" +
            automation.productions[secondProductionId].toString(automation) +
            "'";
    }

}