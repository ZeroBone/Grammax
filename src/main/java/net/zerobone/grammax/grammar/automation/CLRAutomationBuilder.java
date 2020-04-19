package net.zerobone.grammax.grammar.automation;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.lr.LRItemTransition;
import net.zerobone.grammax.grammar.lr.lr1.LR1ClosureCalculation;
import net.zerobone.grammax.grammar.lr.lr1.LR1Items;
import net.zerobone.grammax.grammar.point.LookaheadPoint;

import java.util.HashMap;
import java.util.HashSet;

public class CLRAutomationBuilder {

    private final Automation automation;

    public CLRAutomationBuilder(Grammar grammar) {

        LR1Items items = new LR1Items(grammar);

        automation = new Automation(grammar, items.getStateCount());

        initializeStates(grammar, items);

        // fill the tables

        writeShifts(items);

        writeReduces(grammar, items);

    }

    private void initializeStates(Grammar grammar, LR1Items items) {

        for (HashMap.Entry<HashSet<LookaheadPoint>, Integer> entry : items.getStates()) {

            HashSet<LookaheadPoint> kernels = entry.getKey();

            int stateId = entry.getValue();

            StringBuilder sb = new StringBuilder();

            for (LookaheadPoint point : kernels) {

                Production production = grammar.getProduction(point.productionId);

                sb.append("    ");

                sb.append(production.stringifyWithPointMarker(point.position));

                sb.append(", ");

                sb.append(Symbol.prettyPrintSet(point.lookahead));

                sb.append('\n');

            }

            automation.setParsingStateDescription(stateId, sb.toString());

        }

    }

    private void writeShifts(LR1Items items) {

        for (LRItemTransition transition : items.getTransitions()) {

            if (transition.grammarSymbol.isTerminal) {
                // write to action table

                automation.writeShift(transition.state, transition.grammarSymbol, transition.targetState);

            }
            else {
                // write to goto table

                automation.writeGoto(transition.state, transition.grammarSymbol, transition.targetState);

            }

        }

    }

    private void writeReduces(Grammar grammar, LR1Items items) {

        for (HashMap.Entry<HashSet<LookaheadPoint>, Integer> entry : items.getStates()) {

            HashSet<LookaheadPoint> derivative = entry.getKey();

            assert !derivative.isEmpty();

            int stateId = entry.getValue();

            HashSet<LookaheadPoint> endPoints = LR1ClosureCalculation.endPointClosure(grammar, derivative);

            System.out.println("[LOG]: LR1 state: " + stateId + " Derivative: " + derivative + " End points: " + endPoints);

            for (LookaheadPoint kernelPoint : endPoints) {

                Production pointProduction = grammar.getProduction(kernelPoint.productionId);

                assert kernelPoint.position == pointProduction.body.size();

                if (grammar.getProduction(kernelPoint.productionId).getNonTerminal() == grammar.getStartSymbol()) {

                    automation.writeAccept(stateId);

                    continue;
                }

                for (Symbol terminalOrEof : kernelPoint.lookahead) {

                    automation.writeReduce(stateId, terminalOrEof, kernelPoint.productionId);

                }

            }

        }

    }

    public Automation build() {
        return automation;
    }

}