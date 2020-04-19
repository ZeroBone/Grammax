package net.zerobone.grammax.grammar.lr.lr0;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.ProductionSymbol;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.point.LookaheadPoint;
import net.zerobone.grammax.grammar.point.Point;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class LR0ClosureCalculation {

    private LR0ClosureCalculation() {}

    public static HashSet<Point> closure(Grammar grammar, HashSet<Point> kernels) {
        return calculateClosure(grammar, kernels, false);
    }

    // calculates the closure with only productions which have a point at the end
    // because all the productions in the closure have the point at the start,
    // this could only happen if we have an epsilon production
    // so in other words this method calculates the closure with the epsilon productions
    public static HashSet<Point> endPointClosure(Grammar grammar, HashSet<Point> kernels) {
        return calculateClosure(grammar, kernels, true);
    }

    private static HashSet<Point> calculateClosure(Grammar grammar, HashSet<Point> kernels, boolean onlyEndPoint) {

        HashSet<Symbol> added = new HashSet<>(grammar.getNonTerminalCount());

        Queue<Symbol> pendingNonTerminals = new LinkedList<>();

        for (Point point : kernels) {

            ProductionSymbol symbolAfterPoint = grammar.getSymbolAfter(point);

            if (symbolAfterPoint == null) {
                // the point reached the end of the production
                continue;
            }

            if (symbolAfterPoint.symbol.isTerminal) {
                continue;
            }

            // non-terminal

            if (added.contains(symbolAfterPoint.symbol)) {
                continue;
            }

            pendingNonTerminals.add(symbolAfterPoint.symbol);

            added.add(symbolAfterPoint.symbol);

        }

        HashSet<Point> closure;

        if (onlyEndPoint) {

            closure = new HashSet<>();

            for (Point kernel : kernels) {

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

            Symbol nonTerminal = pendingNonTerminals.poll();
            assert !nonTerminal.isTerminal;

            for (Iterator<Integer> it = grammar.getProductionIdsFor(nonTerminal); it.hasNext();) {

                int productionId = it.next();

                Production production = grammar.getProduction(productionId);

                assert production != null;

                if (production.body.isEmpty()) {
                    closure.add(new Point(productionId, 0));
                    continue;
                }

                if (!onlyEndPoint) {
                    closure.add(new Point(productionId, 0));
                }

                ProductionSymbol firstSymbol = production.body.get(0);

                if (firstSymbol.symbol.isTerminal) {
                    continue;
                }

                // first symbol is a non-terminal

                if (added.contains(firstSymbol.symbol)) {
                    continue;
                }

                pendingNonTerminals.add(firstSymbol.symbol);

                added.add(firstSymbol.symbol);

            }

        }

        return closure;

    }

}