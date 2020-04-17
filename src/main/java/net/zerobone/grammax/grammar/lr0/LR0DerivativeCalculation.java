package net.zerobone.grammax.grammar.lr0;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.ProductionSymbol;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.utils.Point;

import java.util.HashMap;
import java.util.HashSet;

public class LR0DerivativeCalculation {

    private LR0DerivativeCalculation() {}

    public static HashMap<Symbol, HashSet<Point>> calculateAllDerivatives(Grammar grammar, HashSet<Point> kernels) {

        HashSet<Point> closure = LR0ClosureCalculation.closure(grammar, kernels);

        HashMap<Symbol, HashSet<Point>> derivatives = new HashMap<>();

        for (Point point : closure) {

            Production production = grammar.getProduction(point.productionId);

            assert production != null;
            assert point.position <= production.body.size();

            if (point.position == production.body.size()) {
                // point is at the end of the production
                continue;
            }

            ProductionSymbol symbolAfterPoint = production.body.get(point.position);

            HashSet<Point> correspondingDerivative = derivatives.computeIfAbsent(symbolAfterPoint.symbol, k -> new HashSet<>());

            correspondingDerivative.add(new Point(point.productionId, point.position + 1));

        }

        return derivatives;

    }

}