package net.zerobone.grammax.grammar.lr;

public class LRItemTransition {

    public final int state;

    public final int grammarSymbol;

    public final int targetState;

    public LRItemTransition(int state, int grammarSymbol, int targetState) {
        this.state = state;
        this.grammarSymbol = grammarSymbol;
        this.targetState = targetState;
    }

}