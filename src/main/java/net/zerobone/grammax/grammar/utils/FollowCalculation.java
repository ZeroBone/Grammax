package net.zerobone.grammax.grammar.utils;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.ProductionSymbol;
import net.zerobone.grammax.grammar.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FollowCalculation {

    private final Grammar grammar;

    private final HashMap<Symbol, HashSet<Symbol>> followSets = new HashMap<>();

    private FollowCalculation(Grammar grammar) {
        this.grammar = grammar;
    }

    private void computeFollowSets() {

        for (Map.Entry<Symbol, ArrayList<Integer>> pair : grammar.getProductions()) {

            Symbol nonTerminal = pair.getKey();

            followSets.put(nonTerminal, initializeFollowSet(nonTerminal));

        }

        followSets
            .get(grammar.getStartSymbol())
            .add(Symbol.EOF);

        while (true) {
            if (!updateFollowSets()) {
                break;
            }
        }

    }

    private HashSet<Symbol> initializeFollowSet(Symbol nonTerminal) {

        HashSet<Symbol> set = new HashSet<>();

        for (ArrayList<Integer> currentProductions : grammar.getProductionIds()) {

            for (int productionId : currentProductions) {

                Production production = grammar.getProduction(productionId);

                for (int i = 0; i < production.body.size();) {

                    ProductionSymbol symbol = production.body.get(i);

                    if (symbol.symbol != nonTerminal) {
                        i++;
                        continue;
                    }

                    assert !symbol.symbol.isTerminal;

                    if (i == production.body.size() - 1) {
                        // epsilon tokens are never present in follow sets
                        break;
                    }

                    ProductionSymbol nextSymbol = production.body.get(i + 1);

                    if (nextSymbol.symbol.isTerminal) {
                        set.add(nextSymbol.symbol);
                    }

                    i += 2;

                }

            }

        }

        return set;

    }

    private boolean updateFollowSets() {

        boolean modified = false;

        for (Map.Entry<Symbol, ArrayList<Integer>> pair : grammar.getProductions()) {

            Symbol productionLabel = pair.getKey();

            ArrayList<Integer> thisLabelProductions = pair.getValue();

            for (int productionId : thisLabelProductions) {

                Production production = grammar.getProduction(productionId);

                for (int i = 0; i < production.body.size();) {

                    ProductionSymbol symbol = production.body.get(i);

                    if (symbol.symbol.isTerminal) {
                        i++;
                        continue;
                    }

                    // we found a production either of the form alpha A beta
                    // or alpha A
                    // examine the next symbol to find out

                    if (i == production.body.size() - 1) {
                        // beta = epsilon
                        // so we are in the alpha A situation

                        HashSet<Symbol> followSet = followSets.get(symbol.symbol);

                        // System.out.println("Adding followset for " + productionLabel + " -> " + production.toString(this));
                        if (followSet.addAll(followSets.get(productionLabel))) {
                            modified = true;
                        }

                        break;

                    }

                    // we are in the alpha A beta

                    ProductionSymbol nextSymbol = production.body.get(i + 1);

                    if (nextSymbol.symbol.isTerminal) {
                        i += 2;
                        continue;
                    }

                    // nextSymbol (aka beta) is a nonterminal

                    HashSet<Symbol> nextSymbolFirstSet = grammar
                        .firstSets()
                        .get(nextSymbol.symbol);

                    HashSet<Symbol> followSet = followSets.get(symbol.symbol);

                    int oldSize = followSet.size();

                    // check if there is epsilon in the set
                    if (nextSymbolFirstSet.contains(Symbol.EPSILON)) {

                        // union with FIRST(beta) \ epsilon
                        followSet.addAll(nextSymbolFirstSet);
                        followSet.remove(Symbol.EPSILON);

                        // union with FOLLOW(A)
                        followSet.addAll(followSets.get(productionLabel));

                    }
                    else {

                        followSet.addAll(nextSymbolFirstSet);
                        // we don't need to remove epsilon as we already handled this case

                    }

                    if (followSet.size() > oldSize) {
                        modified = true;
                    }

                    i++;

                }

            }

        }

        return modified;

    }

    public static HashMap<Symbol, HashSet<Symbol>> followSets(Grammar grammar) {

        FollowCalculation calculation = new FollowCalculation(grammar);

        calculation.computeFollowSets();

        return calculation.followSets;

    }

}