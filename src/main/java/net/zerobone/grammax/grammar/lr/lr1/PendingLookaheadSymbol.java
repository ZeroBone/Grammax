package net.zerobone.grammax.grammar.lr.lr1;

import net.zerobone.grammax.grammar.Symbol;

import java.util.HashSet;
import java.util.Objects;

final class PendingLookaheadSymbol {

    public final Symbol symbol;

    public final HashSet<Symbol> lookahead; // contains only terminals or EOF

    public PendingLookaheadSymbol(Symbol symbol, HashSet<Symbol> lookahead) {
        this.symbol = symbol;
        this.lookahead = lookahead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingLookaheadSymbol that = (PendingLookaheadSymbol)o;
        return symbol.equals(that.symbol) &&
            lookahead.equals(that.lookahead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, lookahead);
    }

}