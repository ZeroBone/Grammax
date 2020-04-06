package net.zerobone.grammax.grammar.slr.conflict;

import net.zerobone.grammax.grammar.slr.Automation;

public class ReduceAcceptConflict extends Conflict {

    public final int productionId;

    public ReduceAcceptConflict(int productionId) {
        this.productionId = productionId;
    }

    @Override
    public String toString(Automation automation) {
        return "reduce/reduce conflict: Cannot decide whether to accept or to reduce '" +
            automation.productions[productionId].toString(automation) +
            "'";
    }

}