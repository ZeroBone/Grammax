package net.zerobone.grammax.grammar.utils;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.id.IdSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FollowCalculation {

    private final Grammar grammar;

    private final HashMap<Integer, HashSet<Integer>> followSets = new HashMap<>();

    public FollowCalculation(Grammar grammar) {
        this.grammar = grammar;
    }

    public void computeFollowSets() {

        for (Map.Entry<Integer, ArrayList<Integer>> pair : grammar.getProductions()) {

            int nonTerminal = pair.getKey();

            followSets.put(nonTerminal, initializeFollowSet(nonTerminal));

        }

        followSets
            .get(grammar.getStartSymbol())
            .add(Grammar.TERMINAL_EOF);

        while (true) {
            if (!updateFollowSets()) {
                break;
            }
        }

    }

    private HashSet<Integer> initializeFollowSet(int nonTerminal) {

        HashSet<Integer> set = new HashSet<>();

        for (ArrayList<Integer> currentProductions : grammar.getProductionIds()) {

            for (int productionId : currentProductions) {

                IdProduction production = grammar.getProduction(productionId);

                for (int i = 0; i < production.body.size();) {

                    IdSymbol symbol = production.body.get(i);

                    if (symbol.id != nonTerminal) {
                        i++;
                        continue;
                    }

                    assert !symbol.isTerminal();

                    if (i == production.body.size() - 1) {
                        // epsilon tokens are never present in follow sets
                        break;
                    }

                    IdSymbol nextSymbol = production.body.get(i + 1);

                    if (nextSymbol.isTerminal()) {
                        set.add(nextSymbol.id);
                    }

                    i += 2;

                }

            }

        }

        return set;

    }

    private boolean updateFollowSets() {

        boolean modified = false;

        for (Map.Entry<Integer, ArrayList<Integer>> pair : grammar.getProductions()) {

            int productionLabel = pair.getKey();

            ArrayList<Integer> thisLabelProductions = pair.getValue();

            for (int productionId : thisLabelProductions) {

                IdProduction production = grammar.getProduction(productionId);

                ArrayList<IdSymbol> body = production.body;

                for (int i = 0; i < body.size();) {

                    IdSymbol symbol = body.get(i);

                    if (symbol.isTerminal()) {
                        i++;
                        continue;
                    }

                    if (symbol.id == productionLabel) {
                        i++;
                        continue;
                    }

                    // we found a production either of the form alpha A beta
                    // or alpha A
                    // examine the next symbol to find out

                    if (i == body.size() - 1) {
                        // beta = epsilon
                        // so we are in the alpha A situation

                        HashSet<Integer> followSet = followSets.get(symbol.id);

                        // System.out.println("Adding followset for " + productionLabel + " -> " + production.toString(this));
                        if (followSet.addAll(followSets.get(productionLabel))) {
                            modified = true;
                        }

                        break;
                    }

                    // we are in the alpha A beta

                    IdSymbol nextSymbol = body.get(i + 1);

                    if (nextSymbol.isTerminal()) {
                        i += 2;
                        continue;
                    }

                    // nextSymbol (aka beta) is a nonterminal

                    HashSet<Integer> nextSymbolFirstSet = grammar
                        .firstSets()
                        .get(nextSymbol.id);

                    HashSet<Integer> followSet = followSets.get(symbol.id);

                    int oldSize = followSet.size();

                    // check if there is epsilon in the set
                    if (nextSymbolFirstSet.contains(Grammar.FIRST_FOLLOW_SET_EPSILON)) {

                        // union with FIRST(beta) \ epsilon
                        followSet.addAll(nextSymbolFirstSet);
                        followSet.remove(Grammar.FIRST_FOLLOW_SET_EPSILON);

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

    public HashMap<Integer, HashSet<Integer>> getFollowSets() {
        return followSets;
    }

}