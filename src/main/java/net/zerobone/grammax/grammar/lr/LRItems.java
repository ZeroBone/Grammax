package net.zerobone.grammax.grammar.lr;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.point.BasePoint;

import java.util.*;

public class LRItems<P extends BasePoint> {

    private final HashMap<HashSet<P>, Integer> states = new HashMap<>();

    private final Queue<HashSet<P>> toDerive = new LinkedList<>();

    private final ArrayList<LRItemTransition> transitions = new ArrayList<>();

    private int stateCount = 0;

    public LRItems(Grammar grammar, LRItemsLogic<P> itemsProvider) {
        addInitialState(grammar, itemsProvider);
        populate(grammar, itemsProvider);
    }

    private void addInitialState(Grammar grammar, LRItemsLogic<P> itemsProvider) {

        int startProductionId;

        {
            Iterator<Integer> startSymbolProductions = grammar.getProductionIdsFor(grammar.getStartSymbol());

            assert startSymbolProductions.hasNext() : "grammar is not augmented";

            startProductionId = startSymbolProductions.next();

            assert !startSymbolProductions.hasNext() : "grammar is not augmented";

        }

        HashSet<P> initialKernels = new HashSet<>(1);

        // initialKernels.add(new Point(startProductionId, 0));
        initialKernels.add(itemsProvider.createInitialKernal(startProductionId));

        toDerive.add(initialKernels);

        states.put(initialKernels, stateCount++);

    }

    private void populate(Grammar grammar, LRItemsLogic<P> itemsProvider) {

        assert toDerive.size() == 1;

        do {

            HashSet<P> kernels = toDerive.poll();

            calculateDerivations(grammar, kernels, itemsProvider);

        } while (!toDerive.isEmpty());

    }

    private void calculateDerivations(Grammar grammar, HashSet<P> state, LRItemsLogic<P> itemsProvider) {

        assert states.containsKey(state) : "undefined states should not be added to the queue";

        int stateId = states.get(state);

        // HashMap<Symbol, HashSet<Point>> derivatives = LR0DerivativeCalculation.calculateAllDerivatives(grammar, state);
        HashMap<Symbol, HashSet<P>> derivatives = itemsProvider.calculateAllDerivatives(grammar, state);

        for (Map.Entry<Symbol, HashSet<P>> derivativeEntry : derivatives.entrySet()) {

            Symbol grammarSymbol = derivativeEntry.getKey();

            // kernels of the new state
            HashSet<P> derivative = derivativeEntry.getValue();

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

    public Set<Map.Entry<HashSet<P>, Integer>> getStates() {
        return states.entrySet();
    }

}