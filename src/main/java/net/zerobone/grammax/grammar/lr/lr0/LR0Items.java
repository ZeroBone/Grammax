package net.zerobone.grammax.grammar.lr.lr0;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.lr.LRItems;
import net.zerobone.grammax.grammar.lr.LRItemsLogic;
import net.zerobone.grammax.grammar.point.Point;

import java.util.HashMap;
import java.util.HashSet;

public class LR0Items extends LRItems<Point> {

    private static class Logic implements LRItemsLogic<Point> {

        @Override
        public Point createInitialKernel(int startProductionId) {
            return new Point(startProductionId, 0);
        }

        @Override
        public HashMap<Symbol, HashSet<Point>> calculateAllDerivatives(Grammar grammar, HashSet<Point> state) {
            return LR0DerivativeCalculation.calculateAllDerivatives(grammar, state);
        }

    }

    private static final Logic lr0ItemsLogic = new Logic();

    public LR0Items(Grammar grammar) {
        super(grammar, lr0ItemsLogic);
    }

}