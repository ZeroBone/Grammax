package net.zerobone.grammax.grammar.utils;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.id.IdSymbol;

import java.util.HashMap;
import java.util.HashSet;

public class DerivativeCalculation {

    private final Grammar grammar;

    public DerivativeCalculation(Grammar grammar) {
        this.grammar = grammar;
    }

    public HashMap<Integer, HashSet<Point>> calculateAllDerivatives(HashSet<Point> kernels) {

        HashSet<Point> closure = grammar.lr0PointClosure(kernels);

        HashMap<Integer, HashSet<Point>> derivatives = new HashMap<>();

        for (Point point : closure) {

            IdProduction production = grammar.getProduction(point.productionId);

            assert production != null;

            assert point.position <= production.body.size();

            if (point.position == production.body.size()) {
                // point is at the end of the production
                continue;
            }

            IdSymbol symbolAfterPoint = production.body.get(point.position);

            HashSet<Point> correspondingDerivative = derivatives.computeIfAbsent(symbolAfterPoint.id, k -> new HashSet<>());

            correspondingDerivative.add(new Point(point.productionId, point.position + 1));

        }

        return derivatives;

    }

}