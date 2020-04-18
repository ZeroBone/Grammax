package net.zerobone.grammax.grammar.lr;

import net.zerobone.grammax.grammar.Symbol;

public class LRItemTransition {

    public final int state;

    public final Symbol grammarSymbol;

    public final int targetState;

    public LRItemTransition(int state, Symbol grammarSymbol, int targetState) {
        this.state = state;
        this.grammarSymbol = grammarSymbol;
        this.targetState = targetState;
    }

}