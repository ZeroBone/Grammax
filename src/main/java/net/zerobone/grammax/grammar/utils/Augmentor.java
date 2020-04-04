package net.zerobone.grammax.grammar.utils;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.id.IdSymbol;

public class Augmentor {

    private final Grammar grammar;

    public Augmentor(Grammar grammar) {
        this.grammar = grammar;
    }

    public void augment() {

        int newNonTerminal = grammar.createNonTerminal(grammar.getStartSymbol());

        IdProduction production = new IdProduction(null);

        production.body.add(new IdSymbol(grammar.getStartSymbol(), null));

        grammar.addProduction(newNonTerminal, production);

        grammar.setStartSymbol(newNonTerminal);

    }

}