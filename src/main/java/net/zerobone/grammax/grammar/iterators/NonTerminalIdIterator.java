package net.zerobone.grammax.grammar.iterators;

import java.util.ArrayList;
import java.util.Iterator;

public class NonTerminalIdIterator implements Iterator<Integer> {

    private int nextIndex = 0;

    private final ArrayList<Integer> productions;

    public NonTerminalIdIterator(ArrayList<Integer> productions) {
        this.productions = productions;
    }

    @Override
    public boolean hasNext() {
        assert nextIndex <= productions.size();
        return nextIndex != productions.size();
    }

    @Override
    public Integer next() {
        return productions.get(nextIndex++);
    }

}