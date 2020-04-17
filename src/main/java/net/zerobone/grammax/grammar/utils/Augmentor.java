package net.zerobone.grammax.grammar.utils;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.ProductionSymbol;

public class Augmentor {

    private Augmentor() {}

    public static void augment(Grammar grammar) {

        String newNonTerminal = grammar.createUniqueSymbol(grammar.getStartSymbol().id);

        Production production = new Production(null);

        production.body.add(new ProductionSymbol(grammar.getStartSymbol(), null));

        grammar.addProduction(newNonTerminal, production);

        grammar.setStartSymbol(newNonTerminal);

    }

}