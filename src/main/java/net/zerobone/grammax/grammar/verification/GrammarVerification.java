package net.zerobone.grammax.grammar.verification;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.ProductionSymbol;
import net.zerobone.grammax.grammar.Symbol;
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

        HashSet<Symbol> unvisitedSet = new HashSet<>(grammar.getNonTerminalSymbols());

        HashSet<Symbol> visitedAndOnTheStackSet = new HashSet<>();

        HashSet<Symbol> visitedAndPoppedOutOfStackSet = new HashSet<>();

        HashMap<Symbol, Symbol> parentMap = new HashMap<>();

        Stack<Symbol> stack = new Stack<>();

        while (!unvisitedSet.isEmpty()) {

            Symbol start = unvisitedSet.iterator().next();

            stack.clear();
            visitedAndOnTheStackSet.clear();
            parentMap.clear(); // actually this is not needed

            stack.push(start);
            visitedAndOnTheStackSet.add(start);
            unvisitedSet.remove(start);
            parentMap.put(start, null);

            // dfs

            while (!stack.isEmpty()) {

                Symbol currentNonTerminal = stack.peek();
                assert !currentNonTerminal.isTerminal;

                // find adjacent non-terminal

                Symbol adjacentNonTerminal = null;

                for (Iterator<Production> it = grammar.getProductionsFor(currentNonTerminal); it.hasNext();) {

                    Production production = it.next();

                    if (production.body.isEmpty()) {
                        continue;
                    }

                    ProductionSymbol lastSymbol = production.body.get(production.body.size() - 1);

                    if (lastSymbol.symbol.isTerminal) {
                        continue;
                    }

                    if (visitedAndPoppedOutOfStackSet.contains(lastSymbol.symbol)) {
                        continue;
                    }

                    if (visitedAndOnTheStackSet.contains(lastSymbol.symbol)) {
                        // we found a cycle

                        Symbol cycleEnd = lastSymbol.symbol;
                        Symbol currentNode = currentNonTerminal;

                        // visitedAndOnTheStackSet.remove(cycleEnd);
                        // visitedAndOnTheStackSet.remove(currentNode);

                        LinkedList<Symbol> cycle = new LinkedList<>();

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

                    adjacentNonTerminal = lastSymbol.symbol;

                }

                if (adjacentNonTerminal == null) {

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

        for (Symbol nonTerminal : grammar.getNonTerminalSymbols()) {

            if (grammar.getProductionIdsFor(nonTerminal) == null) {
                messages.add(new NonTerminalNotDefinedMessage(nonTerminal));
            }

        }

    }

    private void verifyNoUnreachableProduction() {

        HashSet<Symbol> unreachableProductions = new HashSet<>(grammar.getNonTerminalSymbols());

        Stack<Symbol> stack = new Stack<>();

        stack.push(grammar.getStartSymbol());

        do {

            Symbol nonTerminal = stack.pop();
            assert !nonTerminal.isTerminal;

            unreachableProductions.remove(nonTerminal);

            for (Iterator<Production> it = grammar.getProductionsFor(nonTerminal); it.hasNext(); ) {

                Production production = it.next();

                for (ProductionSymbol symbol : production.body) {

                    if (symbol.symbol.isTerminal) {
                        continue;
                    }

                    if (!unreachableProductions.contains(symbol.symbol)) {
                        continue;
                    }

                    stack.push(symbol.symbol);

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