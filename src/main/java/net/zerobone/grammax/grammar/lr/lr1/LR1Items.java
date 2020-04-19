package net.zerobone.grammax.grammar.lr.lr1;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.lr.LRItems;
import net.zerobone.grammax.grammar.lr.LRItemsLogic;
import net.zerobone.grammax.grammar.point.LookaheadPoint;

import java.util.HashMap;
import java.util.HashSet;

public class LR1Items extends LRItems<LookaheadPoint> {

    private static class Logic implements LRItemsLogic<LookaheadPoint> {

        @Override
        public LookaheadPoint createInitialKernal(int startProductionId) {

            HashSet<Symbol> initialLookahead = new HashSet<>(1);

            initialLookahead.add(Symbol.EOF);

            return new LookaheadPoint(startProductionId, 0, initialLookahead);

        }

        @Override
        public HashMap<Symbol, HashSet<LookaheadPoint>> calculateAllDerivatives(Grammar grammar, HashSet<LookaheadPoint> state) {
            return LR1DerivativeCalculation.calculateAllDerivatives(grammar, state);
        }

    }

    private static final Logic lr1ItemsLogic = new Logic();

    public LR1Items(Grammar grammar) {
        super(grammar, lr1ItemsLogic);
    }

}