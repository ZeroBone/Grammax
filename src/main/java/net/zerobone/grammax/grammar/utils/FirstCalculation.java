package net.zerobone.grammax.grammar.utils;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.ProductionSymbol;
import net.zerobone.grammax.grammar.Symbol;

import java.util.*;

public class FirstCalculation {

    private final Grammar grammar;

    private final HashMap<Symbol, HashSet<Symbol>> firstSets = new HashMap<>();

    private FirstCalculation(Grammar grammar) {
        this.grammar = grammar;
    }

    private void computeFirstSets() {

        for (Map.Entry<Symbol, ArrayList<Integer>> pair : grammar.getProductions()) {

            Symbol nonTerminal = pair.getKey();

            firstSets.put(nonTerminal, initializeFirstSet(nonTerminal));

        }

        while (true) {
            if (!updateFirstSets()) {
                break;
            }
        }

    }

    private HashSet<Symbol> initializeFirstSet(Symbol nonTerminal) {

        HashSet<Symbol> set = new HashSet<>();

        for (Iterator<Production> it = grammar.getProductionsFor(nonTerminal); it.hasNext();) {

            Production production = it.next();

            if (production.body.isEmpty()) {

                set.add(Symbol.EPSILON);

                continue;

            }

            ProductionSymbol firstSymbol = production.body.get(0);

            if (firstSymbol.symbol.isTerminal) {
                set.add(firstSymbol.symbol);
            }

        }

        return set;

    }

    private boolean updateFirstSets() {

        boolean modified = false;

        for (Map.Entry<Symbol, ArrayList<Integer>> pair : grammar.getProductions()) {

            Symbol label = pair.getKey();

            HashSet<Symbol> labelFirstSet = firstSets.get(label);

            for (int productionId : pair.getValue()) {

                Production production = grammar.getProduction(productionId);

                if (production.body.isEmpty()) {
                    continue;
                }

                for (ProductionSymbol symbol : production.body) {

                    if (symbol.symbol.isTerminal) {
                        if (labelFirstSet.add(symbol.symbol)) {
                            modified = true;
                        }
                        break;
                    }

                    // non-terminal

                    HashSet<Symbol> currentFirstSet = firstSets.get(symbol.symbol);

                    if (labelFirstSet.addAll(currentFirstSet)) {
                        modified = true;
                    }

                    if (!currentFirstSet.contains(Symbol.EPSILON)) {
                        // the current nonterminal doesn't contain epsilon, so we don't need to move on to the next terminal
                        break;
                    }

                    // the current nonterminal is nullable, so it is possible that the next ones appear at the start
                    // so we move on to the next symbol in the production

                }

            }

        }

        return modified;

    }

    public static HashMap<Symbol, HashSet<Symbol>> firstSets(Grammar grammar) {

        FirstCalculation calculation = new FirstCalculation(grammar);

        calculation.computeFirstSets();

        return calculation.firstSets;

    }

}