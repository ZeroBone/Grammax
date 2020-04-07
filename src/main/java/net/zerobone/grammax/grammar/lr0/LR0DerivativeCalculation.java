package net.zerobone.grammax.grammar.lr0;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.id.IdSymbol;
import net.zerobone.grammax.grammar.utils.Point;

import java.util.HashMap;
import java.util.HashSet;

public class LR0DerivativeCalculation {

    private LR0DerivativeCalculation() {}

    public static HashMap<Integer, HashSet<Point>> calculateAllDerivatives(Grammar grammar, HashSet<Point> kernels) {

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