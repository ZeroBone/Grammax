package net.zerobone.grammax.grammar.lr.lr1;

import net.zerobone.grammax.grammar.Symbol;

import java.util.HashSet;

final class PendingLookaheadSymbol {

    public final Symbol symbol;

    public final HashSet<Symbol> lookahead; // contains only terminals or EOF

    public PendingLookaheadSymbol(Symbol symbol, HashSet<Symbol> lookahead) {
        this.symbol = symbol;
        this.lookahead = lookahead;
    }

}