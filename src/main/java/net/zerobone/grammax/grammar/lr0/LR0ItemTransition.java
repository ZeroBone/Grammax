package net.zerobone.grammax.grammar.lr0;

public class LR0ItemTransition {

    public final int state;

    public final int grammarSymbol;

    public final int targetState;

    public LR0ItemTransition(int state, int grammarSymbol, int targetState) {
        this.state = state;
        this.grammarSymbol = grammarSymbol;
        this.targetState = targetState;
    }

}