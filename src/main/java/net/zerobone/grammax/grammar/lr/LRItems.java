package net.zerobone.grammax.grammar.lr;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.utils.DerivativeCalculation;
import net.zerobone.grammax.grammar.utils.Point;

import java.util.*;

public class LRItems {

    public final HashSet<HashSet<Point>> states = new HashSet<>();

    public LRItems(Grammar grammar) {
        addInitialState(grammar);
        populate(grammar);
    }

    private void addInitialState(Grammar grammar) {

        Iterator<Integer> it = grammar.nonTerminalProductionsIds(grammar.getStartSymbol());

        assert it.hasNext();

        int productionId = it.next();

        assert !it.hasNext();

        HashSet<Point> initialKernels = new HashSet<>(1);

        initialKernels.add(new Point(productionId, 0));

        states.add(initialKernels);

    }

    private void populate(Grammar grammar) {

        DerivativeCalculation derivativeCalculation = new DerivativeCalculation(grammar);

        ArrayList<HashSet<Point>> newSets = new ArrayList<>();

        while (true) {

            for (Iterator<HashSet<Point>> it = states.iterator(); it.hasNext();) {

                HashSet<Point> state = it.next();

                HashMap<Integer, HashSet<Point>> derivatives = derivativeCalculation.calculateAllDerivatives(state);

                for (HashMap.Entry<Integer, HashSet<Point>> entry : derivatives.entrySet()) {

                    int grammarSymbol = entry.getKey();

                    // kernels of the new state
                    HashSet<Point> newState = entry.getValue();

                    if (states.contains(newState)) {

                    }
                    else {
                        newSets.add(newState);
                    }

                }

            }

            if (newSets.isEmpty()) {
                break;
            }

            newSets.clear();

        }

    }

}