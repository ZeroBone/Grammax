package net.zerobone.grammax.grammar.verification;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.id.IdSymbol;
import net.zerobone.grammax.grammar.verification.messages.RightRecursiveCycleMessage;
import net.zerobone.grammax.grammar.verification.messages.NonTerminalNotDefinedMessage;
import net.zerobone.grammax.grammar.verification.messages.UnreachableNonTerminalsMessage;
import net.zerobone.grammax.grammar.verification.messages.VerificationMessage;

import java.util.*;

public class GrammarVerification {

    private final Grammar grammar;

    private ArrayList<VerificationMessage> messages = new ArrayList<>();

    public GrammarVerification(Grammar grammar) {
        this.grammar = grammar;
    }

    private void verifyNoRightRecursion() {

        HashSet<Integer> unvisitedSet = new HashSet<>(grammar.getNonTerminals());

        HashSet<Integer> visitedAndOnTheStackSet = new HashSet<>();

        HashSet<Integer> visitedAndPoppedOutOfStackSet = new HashSet<>();

        HashMap<Integer, Integer> parentMap = new HashMap<>();

        Stack<Integer> stack = new Stack<>();

        while (!unvisitedSet.isEmpty()) {

            int start = unvisitedSet.iterator().next();

            stack.clear();
            visitedAndOnTheStackSet.clear();
            parentMap.clear(); // actually this is not needed

            stack.push(start);
            visitedAndOnTheStackSet.add(start);
            unvisitedSet.remove(start);
            parentMap.put(start, null);

            // dfs

            while (!stack.isEmpty()) {

                int currentNonTerminal = stack.peek();

                // find adjacent non-terminal

                int adjacentNonTerminal = 0;

                for (Integer productionId : grammar.getProductionsFor(currentNonTerminal)) {

                    IdProduction production = grammar.getProduction(productionId);

                    if (production.body.isEmpty()) {
                        continue;
                    }

                    IdSymbol lastSymbol = production.body.get(production.body.size() - 1);

                    if (lastSymbol.isTerminal()) {
                        continue;
                    }

                    if (visitedAndPoppedOutOfStackSet.contains(lastSymbol.id)) {
                        continue;
                    }

                    if (visitedAndOnTheStackSet.contains(lastSymbol.id)) {
                        // we found a cycle

                        int cycleEnd = lastSymbol.id;
                        int currentNode = currentNonTerminal;

                        // visitedAndOnTheStackSet.remove(cycleEnd);
                        // visitedAndOnTheStackSet.remove(currentNode);

                        LinkedList<Integer> cycle = new LinkedList<>();

                        cycle.addFirst(cycleEnd);
                        cycle.addFirst(currentNode);

                        while (currentNode != cycleEnd) {
                            currentNode = parentMap.get(currentNode);
                            // visitedAndOnTheStackSet.remove(currentNode);
                            cycle.addFirst(currentNode);
                        }

                        messages.add(
                            new RightRecursiveCycleMessage(cycle)
                        );

                        // return;
                        continue;

                    }

                    adjacentNonTerminal = lastSymbol.id;

                }

                if (adjacentNonTerminal == 0) {

                    // didn't find any other node

                    visitedAndOnTheStackSet.remove(currentNonTerminal);

                    visitedAndPoppedOutOfStackSet.add(currentNonTerminal);

                    stack.pop();

                    continue;
                }

                stack.push(adjacentNonTerminal);

                unvisitedSet.remove(adjacentNonTerminal);

                visitedAndOnTheStackSet.add(adjacentNonTerminal);

                parentMap.put(adjacentNonTerminal, currentNonTerminal);

            }

        }

    }

    private void verifyAllNonterminalsDefined() {

        for (int nonTerminal : grammar.getNonTerminals()) {

            if (grammar.getProductionsFor(nonTerminal) == null) {
                messages.add(new NonTerminalNotDefinedMessage(nonTerminal));
            }

        }

    }

    private void verifyNoUnreachableProduction() {

        HashSet<Integer> unreachableProductions = new HashSet<>(grammar.getNonTerminals());

        Stack<Integer> stack = new Stack<>();

        stack.push(grammar.getStartSymbol());

        do {

            int nonTerminal = stack.pop();

            unreachableProductions.remove(nonTerminal);

            for (int productionId : grammar.getProductionsFor(nonTerminal)) {

                IdProduction production = grammar.getProduction(productionId);

                for (IdSymbol symbol : production.body) {

                    if (symbol.isTerminal()) {
                        continue;
                    }

                    if (!unreachableProductions.contains(symbol.id)) {
                        continue;
                    }

                    stack.push(symbol.id);

                }

            }

        } while (!stack.isEmpty());

        if (unreachableProductions.isEmpty()) {
            return;
        }

        messages.add(new UnreachableNonTerminalsMessage(unreachableProductions));

    }

    public void verify() {

        verifyAllNonterminalsDefined();

        if (!messages.isEmpty()) {
            return;
        }

        verifyNoUnreachableProduction();

        if (!messages.isEmpty()) {
            return;
        }

        verifyNoRightRecursion();

    }

    public ArrayList<VerificationMessage> getMessages() {
        return messages;
    }

}