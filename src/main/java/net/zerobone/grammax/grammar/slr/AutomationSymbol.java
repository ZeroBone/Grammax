package net.zerobone.grammax.grammar.slr;

public class AutomationSymbol {

    public final boolean isTerminal;

    public final int index;

    public final String argumentName;

    public AutomationSymbol(boolean isTerminal, int index, String argumentName) {
        this.isTerminal = isTerminal;
        this.index = index;
        this.argumentName = argumentName;
    }

}