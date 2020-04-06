package net.zerobone.grammax.grammar.slr.conflict;

import net.zerobone.grammax.grammar.slr.Automation;

public class ShiftOption extends ConflictOption {

    public final int terminalIndex;

    public ShiftOption(int terminalIndex) {
        super(SHORT_NAME_SHIFT);
        this.terminalIndex = terminalIndex;
    }

    @Override
    public String toString(Automation automation) {
        return "shift " + automation.terminalToSymbol(terminalIndex);
    }

}