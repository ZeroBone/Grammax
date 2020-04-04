package net.zerobone.grammax.grammar.point;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.id.IdSymbol;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class PointClosure {

    private final Grammar grammar;

    public PointClosure(Grammar grammar) {
        this.grammar = grammar;
    }

    public HashSet<Point> closure(HashSet<Point> kernels) {

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

        HashSet<Point> closure = new HashSet<>(kernels);

        // bfs

        while (!pendingNonTerminals.isEmpty()) {

            int nonTerminal = pendingNonTerminals.poll();

            for (Iterator<Integer> it = grammar.nonTerminalProductionsIds(nonTerminal); it.hasNext();) {

                int productionId = it.next();

                closure.add(new Point(productionId, 0));

                IdProduction production = grammar.getProduction(productionId);

                assert production != null;

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