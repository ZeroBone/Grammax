package net.zerobone.grammax.grammar.iterators;

import net.zerobone.grammax.grammar.id.IdGrammar;
import net.zerobone.grammax.grammar.id.IdProduction;

import java.util.ArrayList;
import java.util.Iterator;

public class NonTerminalIterator implements Iterator<IdProduction> {

    private int nextIndex = 0;

    private final ArrayList<Integer> productions;

    private final IdGrammar grammar;

    public NonTerminalIterator(ArrayList<Integer> productions, IdGrammar grammar) {
        this.productions = productions;
        this.grammar = grammar;
    }

    @Override
    public boolean hasNext() {
        assert nextIndex <= productions.size();
        return nextIndex != productions.size();
    }

    @Override
    public IdProduction next() {

        int productionId = productions.get(nextIndex++);

        return grammar.getProduction(productionId);

    }

}