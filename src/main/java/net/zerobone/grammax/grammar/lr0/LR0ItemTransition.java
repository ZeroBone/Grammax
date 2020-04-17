package net.zerobone.grammax.grammar.lr0;

import net.zerobone.grammax.grammar.Symbol;

public class LR0ItemTransition {

    public final int state;

    public final Symbol grammarSymbol;

    public final int targetState;

    public LR0ItemTransition(int state, Symbol grammarSymbol, int targetState) {
        this.state = state;
        this.grammarSymbol = grammarSymbol;
        this.targetState = targetState;
    }

}