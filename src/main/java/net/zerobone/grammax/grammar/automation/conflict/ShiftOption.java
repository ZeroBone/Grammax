package net.zerobone.grammax.grammar.automation.conflict;

import net.zerobone.grammax.grammar.automation.Automation;

public class ShiftOption extends ConflictOption {

    public final int terminalIndex;

    public ShiftOption(int terminalIndex) {
        super(SHORT_NAME_SHIFT);
        this.terminalIndex = terminalIndex;
    }

    @Override
    public String toString(Automation automation) {
        return "shift '" + automation.terminalToSymbol(terminalIndex) + "'";
    }

}