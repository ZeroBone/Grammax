package net.zerobone.grammax.grammar.slr.conflict;

import net.zerobone.grammax.grammar.slr.Automation;

public class ReduceOption extends ConflictOption {

    private final int productionIndex;

    public ReduceOption(int productionIndex) {
        super(SHORT_NAME_REDUCE);
        this.productionIndex = productionIndex;
    }

    @Override
    public String toString(Automation automation) {
        return "reduce '" + automation.productions[productionIndex].toString(automation) + "'";
    }

}