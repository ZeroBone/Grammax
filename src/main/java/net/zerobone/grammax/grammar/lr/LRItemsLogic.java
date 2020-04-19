package net.zerobone.grammax.grammar.lr;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.point.BasePoint;

import java.util.HashMap;
import java.util.HashSet;

public interface LRItemsLogic<P extends BasePoint> {

    P createInitialKernal(int startProductionId);

    HashMap<Symbol, HashSet<P>> calculateAllDerivatives(Grammar grammar, HashSet<P> state);

}