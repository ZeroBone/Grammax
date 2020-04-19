package net.zerobone.grammax.grammar.lr.lr1;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.ProductionSymbol;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.point.LookaheadPoint;

import java.util.HashMap;
import java.util.HashSet;

class LR1DerivativeCalculation {

    static HashMap<Symbol, HashSet<LookaheadPoint>> calculateAllDerivatives(Grammar grammar, HashSet<LookaheadPoint> kernels) {

        HashSet<LookaheadPoint> closure = LR1ClosureCalculation.closure(grammar, kernels);

        HashMap<Symbol, HashSet<LookaheadPoint>> derivatives = new HashMap<>();

        for (LookaheadPoint point : closure) {

            Production production = grammar.getProduction(point.productionId);

            assert production != null;
            assert point.position <= production.body.size();

            if (point.position == production.body.size()) {
                // point is at the end of the production
                continue;
            }

            ProductionSymbol symbolAfterPoint = production.body.get(point.position);

            HashSet<LookaheadPoint> correspondingDerivative = derivatives.computeIfAbsent(symbolAfterPoint.symbol, k -> new HashSet<>());

            correspondingDerivative.add(
                new LookaheadPoint(point.productionId, point.position + 1, point.lookahead)
            );

        }

        return derivatives;

    }

}