package net.zerobone.grammax.grammar.lr.lr1;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.ProductionSymbol;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.point.LookaheadPoint;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class LR1ClosureCalculation {

    private LR1ClosureCalculation() {}

    public static HashSet<LookaheadPoint> closure(Grammar grammar, HashSet<LookaheadPoint> kernels) {
        return calculateClosure(grammar, kernels, false);
    }

    public static HashSet<LookaheadPoint> endPointClosure(Grammar grammar, HashSet<LookaheadPoint> kernels) {
        return calculateClosure(grammar, kernels, true);
    }

    private static HashSet<Symbol> calculateFirstAfter(Grammar grammar, Production production, int startPosition, HashSet<Symbol> lookahead) {

        HashSet<Symbol> firstSet = new HashSet<>();

        for (int i = startPosition; i < production.body.size(); i++) {

            ProductionSymbol currentSymbol = production.body.get(i);

            if (currentSymbol.symbol.isTerminal) {
                firstSet.add(currentSymbol.symbol);
                return firstSet;
            }

            // current symbol is a non-terminal

            HashSet<Symbol> currentSymbolFirstSet = grammar.firstSet(currentSymbol.symbol);

            if (currentSymbolFirstSet.contains(Symbol.EPSILON)) {
                // we will need to proceed because this symbol contains epsilon so it is possible
                // that a derivation starts with some terminal in the first set of the next grammar
                // symbol(s) and/or (in case we are at the end of the production) the lookahead
                firstSet.addAll(currentSymbolFirstSet);
                firstSet.remove(Symbol.EPSILON);
            }
            else {
                // no need to proceed
                firstSet.addAll(currentSymbolFirstSet);
                return firstSet;
            }

        }

        // all the grammar symbols contained epsilon in their first sets
        // so we need to add the terminals from the lookahead because it can show up in some derivation

        firstSet.addAll(lookahead);

        return firstSet;

    }

    private static HashSet<LookaheadPoint> calculateClosure(Grammar grammar, HashSet<LookaheadPoint> kernels, boolean onlyEndPoint) {

        HashSet<Symbol> added = new HashSet<>(grammar.getNonTerminalCount());

        Queue<PendingLookaheadSymbol> pendingNonTerminals = new LinkedList<>();

        for (LookaheadPoint point : kernels) {

            Production production = grammar.getProduction(point.productionId);

            assert point.position <= production.body.size();

            if (point.position == production.body.size()) {
                // the point reached the end of the production
                continue;
            }

            assert point.position < production.body.size();

            ProductionSymbol symbolAfterPoint = production.body.get(point.position);

            if (symbolAfterPoint.symbol.isTerminal) {
                continue;
            }

            // non-terminal

            if (added.contains(symbolAfterPoint.symbol)) {
                continue;
            }

            HashSet<Symbol> newLookahead = calculateFirstAfter(
                grammar,
                production,
                point.position + 1,
                point.lookahead
            );

            pendingNonTerminals.add(
                new PendingLookaheadSymbol(symbolAfterPoint.symbol, newLookahead)
            );

            added.add(symbolAfterPoint.symbol);

        }

        HashSet<LookaheadPoint> closure;

        if (onlyEndPoint) {

            closure = new HashSet<>();

            for (LookaheadPoint kernel : kernels) {

                Production production = grammar.getProduction(kernel.productionId);

                assert kernel.position <= production.body.size();

                if (kernel.position != production.body.size()) {
                    assert kernel.position < production.body.size();
                    continue;
                }

                // the point is at the end of the production
                closure.add(kernel);

            }

        }
        else {
            closure = new HashSet<>(kernels);
        }

        // bfs

        while (!pendingNonTerminals.isEmpty()) {

            Symbol nonTerminal;
            HashSet<Symbol> lookahead;

            {
                PendingLookaheadSymbol pendingLookahead = pendingNonTerminals.poll();

                nonTerminal = pendingLookahead.symbol;
                lookahead = pendingLookahead.lookahead;
            }

            assert !nonTerminal.isTerminal;

            for (Iterator<Integer> it = grammar.getProductionIdsFor(nonTerminal); it.hasNext();) {

                int productionId = it.next();

                Production production = grammar.getProduction(productionId);

                assert production != null;

                if (production.body.isEmpty()) {
                    closure.add(new LookaheadPoint(productionId, 0, lookahead));
                    continue;
                }

                if (!onlyEndPoint) {
                    closure.add(new LookaheadPoint(productionId, 0, lookahead));
                }

                ProductionSymbol firstSymbol = production.body.get(0);

                if (firstSymbol.symbol.isTerminal) {
                    continue;
                }

                // first symbol is a non-terminal

                if (added.contains(firstSymbol.symbol)) {
                    continue;
                }

                HashSet<Symbol> newLookahead = calculateFirstAfter(
                    grammar,
                    production,
                    1,
                    lookahead
                );

                pendingNonTerminals.add(
                    new PendingLookaheadSymbol(firstSymbol.symbol, newLookahead)
                );

                added.add(firstSymbol.symbol);

            }

        }

        return closure;

    }

}