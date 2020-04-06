package net.zerobone.grammax.grammar.lr;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.utils.Point;

import java.util.*;

public class LRItems {

    private final HashMap<HashSet<Point>, Integer> states = new HashMap<>();

    private final Queue<HashSet<Point>> toDerive = new LinkedList<>();

    private final ArrayList<LRItemTransition> transitions = new ArrayList<>();

    private int stateCount = 0;

    public LRItems(Grammar grammar) {
        addInitialState(grammar);
        populate(grammar);
    }

    private void addInitialState(Grammar grammar) {

        int productionId;

        {
            ArrayList<Integer> startSymbolProductions = grammar.getProductionsFor(grammar.getStartSymbol());

            assert startSymbolProductions.size() == 1 : "grammar is not augmented";

            productionId = startSymbolProductions.get(0);

        }

        HashSet<Point> initialKernels = new HashSet<>(1);

        initialKernels.add(new Point(productionId, 0));

        toDerive.add(initialKernels);

        states.put(initialKernels, stateCount++);

    }

    private void calculateDerivations(Grammar grammar, HashSet<Point> state) {

        assert states.containsKey(state) : "undefined states should not be added to the queue";

        int stateId = states.get(state);

        HashMap<Integer, HashSet<Point>> derivatives = grammar.calculateAllLr0Derivatives(state);

        for (HashMap.Entry<Integer, HashSet<Point>> derivativeEntry : derivatives.entrySet()) {

            int grammarSymbol = derivativeEntry.getKey();

            // kernels of the new state
            HashSet<Point> derivative = derivativeEntry.getValue();

            Integer derivativeStateId = states.get(derivative);

            if (derivativeStateId == null) {

                // new derivative

                toDerive.add(derivative);

                derivativeStateId = stateCount++;

                states.put(derivative, derivativeStateId);

            }

            System.out.println("[LOG]: Deriving: " + stateId + " / " + grammar.idToSymbol(grammarSymbol) + " = " + derivativeStateId);

            transitions.add(new LRItemTransition(stateId, grammarSymbol, derivativeStateId));

        }

    }

    private void populate(Grammar grammar) {

        do {

            HashSet<Point> kernels = toDerive.poll();

            calculateDerivations(grammar, kernels);

        } while (!toDerive.isEmpty());

    }

    public ArrayList<LRItemTransition> getTransitions() {
        return transitions;
    }

    public int getStateCount() {
        return stateCount;
    }

    public Set<Map.Entry<HashSet<Point>, Integer>> getStates() {
        return states.entrySet();
    }

}