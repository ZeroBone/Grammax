package net.zerobone.grammax.grammar.lr0;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.utils.Point;

import java.util.*;

public class LR0Items {

    private final HashMap<HashSet<Point>, Integer> states = new HashMap<>();

    private final Queue<HashSet<Point>> toDerive = new LinkedList<>();

    private final ArrayList<LR0ItemTransition> transitions = new ArrayList<>();

    private int stateCount = 0;

    public LR0Items(Grammar grammar) {
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

    private void populate(Grammar grammar) {

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

            assert debug_derivativeValid(derivative);

            Integer derivativeStateId = states.get(derivative);

            if (derivativeStateId == null) {

                // new derivative

                toDerive.add(derivative);

                derivativeStateId = stateCount++;

                states.put(derivative, derivativeStateId);

            }

            System.out.println("[LOG]: Deriving: " + stateId + " / " + grammarSymbol.id + " = " + derivativeStateId);

            transitions.add(new LR0ItemTransition(stateId, grammarSymbol, derivativeStateId));

        }

    }

    public ArrayList<LR0ItemTransition> getTransitions() {
        return transitions;
    }

    public int getStateCount() {
        return stateCount;
    }

    public Set<Map.Entry<HashSet<Point>, Integer>> getStates() {
        return states.entrySet();
    }

    public static boolean debug_derivativeValid(HashSet<Point> derivative) {

        if (derivative.isEmpty()) {
            return false;
        }

        HashSet<Integer> pointProductions = new HashSet<>();

        for (Point point : derivative) {
            if (pointProductions.contains(point.productionId)) {
                System.err.println("Duplicate derivative for production " + point.productionId);
                return false;
            }
            pointProductions.add(point.productionId);
        }

        return true;

    }

}