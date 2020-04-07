package net.zerobone.grammax.grammar.lr0;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.id.IdSymbol;
import net.zerobone.grammax.grammar.utils.Point;

import java.util.HashSet;
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

        boolean[] added = new boolean[grammar.getNonTerminalCount()];

        Queue<Integer> pendingNonTerminals = new LinkedList<>();

        for (Point point : kernels) {

            IdSymbol symbolAfterPoint = grammar.getSymbolAfter(point);

            if (symbolAfterPoint == null) {
                // the point reached the end of the production
                continue;
            }

            if (symbolAfterPoint.isTerminal()) {
                continue;
            }

            // non-terminal

            int index = Grammar.nonTerminalToIndex(symbolAfterPoint.id);

            if (added[index]) {
                continue;
            }

            pendingNonTerminals.add(symbolAfterPoint.id);

            added[index] = true;

        }

        HashSet<Point> closure;

        if (onlyEndPoint) {

            closure = new HashSet<>();

            for (Point kernel : kernels) {

                IdProduction production = grammar.getProduction(kernel.productionId);

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

            int nonTerminal = pendingNonTerminals.poll();

            for (int productionId : grammar.getProductionsFor(nonTerminal)) {

                IdProduction production = grammar.getProduction(productionId);

                assert production != null;

                if (!onlyEndPoint || production.body.isEmpty()) {
                    closure.add(new Point(productionId, 0));
                }

                if (production.body.isEmpty()) {
                    continue;
                }

                IdSymbol firstSymbol = production.body.get(0);

                if (firstSymbol.isTerminal()) {
                    continue;
                }

                // first symbol is a non-terminal

                int firstSymbolIndex = Grammar.nonTerminalToIndex(firstSymbol.id);

                if (added[firstSymbolIndex]) {
                    continue;
                }

                pendingNonTerminals.add(firstSymbol.id);

                added[firstSymbolIndex] = true;

            }

        }

        return closure;

    }

}