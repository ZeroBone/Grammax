package net.zerobone.grammax.grammar.utils;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.id.IdSymbol;

public class Augmentor {

    private Augmentor() {}

    public static void augment(Grammar grammar) {

        int newNonTerminal = grammar.createNonTerminal(grammar.getStartSymbol());

        IdProduction production = new IdProduction(null);

        production.body.add(new IdSymbol(grammar.getStartSymbol(), null));

        grammar.addProduction(newNonTerminal, production);

        grammar.setStartSymbol(newNonTerminal);

    }

}