package net.zerobone.grammax.grammar.lr;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.point.Point;

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

        int startProductionId;

        {
            Iterator<Integer> startSymbolProductions = grammar.getProductionIdsFor(grammar.getStartSymbol());

            assert startSymbolProductions.hasNext() : "grammar is not augmented";

            startProductionId = startSymbolProductions.next();

            assert !startSymbolProductions.hasNext() : "grammar is not augmented";

        }

        HashSet<Point> initialKernels = new HashSet<>(1);

        initialKernels.add(new Point(startProductionId, 0));

        toDerive.add(initialKernels);

        states.put(initialKernels, stateCount++);

    }

    private void populate(Grammar grammar) {

        assert toDerive.size() == 1;

        do {

            HashSet<Point> kernels = toDerive.poll();

            calculateDerivations(grammar, kernels);

        } while (!toDerive.isEmpty());

    }

    private void calculateDerivations(Grammar grammar, HashSet<Point> state) {

        assert states.containsKey(state) : "undefined states should not be added to the queue";

        int stateId = states.get(state);

        HashMap<Symbol, HashSet<Point>> derivatives = LR0DerivativeCalculation.calculateAllDerivatives(grammar, state);

        for (Map.Entry<Symbol, HashSet<Point>> derivativeEntry : derivatives.entrySet()) {

            Symbol grammarSymbol = derivativeEntry.getKey();

            // kernels of the new state
            HashSet<Point> derivative = derivativeEntry.getValue();

            Integer derivativeStateId = states.get(derivative);

            if (derivativeStateId == null) {

                // new derivative

                toDerive.add(derivative);

                derivativeStateId = stateCount++;

                states.put(derivative, derivativeStateId);

            }

            System.out.println("[LOG]: Deriving: " + stateId + " / " + grammarSymbol.id + " = " + derivativeStateId);

            transitions.add(new LRItemTransition(stateId, grammarSymbol, derivativeStateId));

        }

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