package net.zerobone.grammax.grammar.automation;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.lr.lr0.LR0ClosureCalculation;
import net.zerobone.grammax.grammar.lr.LRItemTransition;
import net.zerobone.grammax.grammar.lr.lr0.LR0Items;
import net.zerobone.grammax.grammar.point.Point;

import java.util.HashMap;
import java.util.HashSet;

public class SLRAutomationBuilder {

    private final Automation automation;

    public SLRAutomationBuilder(Grammar grammar) {

        LR0Items items = new LR0Items(grammar);

        automation = new Automation(grammar, items.getStateCount());

        initializeStates(grammar, items);

        // fill the tables

        writeShifts(items);

        writeReduces(grammar, items);

    }

    private void initializeStates(Grammar grammar, LR0Items items) {

        for (HashMap.Entry<HashSet<Point>, Integer> entry : items.getStates()) {

            HashSet<Point> kernels = entry.getKey();

            int stateId = entry.getValue();

            StringBuilder sb = new StringBuilder();

            for (Point point : kernels) {

                Production production = grammar.getProduction(point.productionId);

                sb.append("    ");

                sb.append(production.stringifyWithPointMarker(point.position));

                sb.append('\n');

            }

            automation.setParsingStateDescription(stateId, sb.toString());

        }

    }

    private void writeShifts(LR0Items items) {

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

    private void writeReduces(Grammar grammar, LR0Items items) {

        for (HashMap.Entry<HashSet<Point>, Integer> entry : items.getStates()) {

            HashSet<Point> derivative = entry.getKey();

            assert !derivative.isEmpty();

            int stateId = entry.getValue();

            HashSet<Point> endPoints = LR0ClosureCalculation.endPointClosure(grammar, derivative);

            System.out.println("[LOG]: LR0 state: " + stateId + " Derivative: " + derivative + " End points: " + endPoints);

            for (Point kernelPoint : endPoints) {

                Production pointProduction = grammar.getProduction(kernelPoint.productionId);

                assert kernelPoint.position == pointProduction.body.size();

                Symbol nonTerminal = grammar.getProduction(kernelPoint.productionId).getNonTerminal();

                if (nonTerminal == grammar.getStartSymbol()) {

                    automation.writeAccept(stateId);

                    continue;
                }

                // compute the follow set of the label of the production with the point at the end

                for (Symbol terminalOrEof : grammar.followSet(nonTerminal)) {

                    automation.writeReduce(stateId, terminalOrEof, kernelPoint.productionId);

                }

            }

        }

    }

    public Automation build() {
        return automation;
    }

}