package net.zerobone.grammax.grammar.utils;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.id.IdSymbol;

import java.util.*;

public class FirstCalculation {

    private final Grammar grammar;

    private final HashMap<Integer, HashSet<Integer>> firstSets = new HashMap<>();

    private FirstCalculation(Grammar grammar) {
        this.grammar = grammar;
    }

    private void computeFirstSets() {

        for (Map.Entry<Integer, ArrayList<Integer>> pair : grammar.getProductions()) {

            int nonTerminal = pair.getKey();

            firstSets.put(nonTerminal, initializeFirstSet(nonTerminal));

        }

        while (true) {
            if (!updateFirstSets()) {
                break;
            }
        }

    }

    private HashSet<Integer> initializeFirstSet(int nonTerminal) {

        HashSet<Integer> set = new HashSet<>();

        for (int productionId : grammar.getProductionsFor(nonTerminal)) {

            IdProduction production = grammar.getProduction(productionId);

            if (production.body.isEmpty()) {

                set.add(Grammar.FIRST_FOLLOW_SET_EPSILON);

                continue;

            }

            IdSymbol firstSymbol = production.body.get(0);

            if (firstSymbol.isTerminal()) {
                set.add(firstSymbol.id);
            }

        }

        return set;

    }

    private boolean updateFirstSets() {

        boolean modified = false;

        for (Map.Entry<Integer, ArrayList<Integer>> pair : grammar.getProductions()) {

            int label = pair.getKey();

            HashSet<Integer> labelFirstSet = firstSets.get(label);

            for (int productionId : pair.getValue()) {

                IdProduction production = grammar.getProduction(productionId);

                ArrayList<IdSymbol> body = production.body;

                if (body.isEmpty()) {
                    continue;
                }

                for (IdSymbol symbol : body) {

                    if (symbol.isTerminal()) {
                        if (labelFirstSet.add(symbol.id)) {
                            modified = true;
                        }
                        break;
                    }

                    // non-terminal

                    HashSet<Integer> currentFirstSet = firstSets.get(symbol.id);

                    if (labelFirstSet.addAll(currentFirstSet)) {
                        modified = true;
                    }

                    if (!currentFirstSet.contains(Grammar.FIRST_FOLLOW_SET_EPSILON)) {
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

    public static HashMap<Integer, HashSet<Integer>> firstSets(Grammar grammar) {

        FirstCalculation calculation = new FirstCalculation(grammar);

        calculation.computeFirstSets();

        return calculation.firstSets;

    }

}