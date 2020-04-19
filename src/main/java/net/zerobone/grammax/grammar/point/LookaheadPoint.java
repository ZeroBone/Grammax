package net.zerobone.grammax.grammar.point;

import net.zerobone.grammax.grammar.Symbol;

import java.util.HashSet;
import java.util.Objects;

// this class is final because we will never mix up different kinds of points
public final class LookaheadPoint extends BasePoint {

    public final HashSet<Symbol> lookahead;

    public LookaheadPoint(int productionId, int position, HashSet<Symbol> lookahead) {
        super(productionId, position);
        assert lookahead != null;
        this.lookahead = lookahead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LookaheadPoint that = (LookaheadPoint)o;
        return lookahead.equals(that.lookahead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lookahead);
    }

    @Override
    public String toString() {
        return "(" + productionId + ", " + position + ", " + Symbol.prettyPrintSet(lookahead) + ")";
    }

}