package net.zerobone.grammax.grammar.slr;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.lr.LRItemTransition;
import net.zerobone.grammax.grammar.lr.LRItems;

import java.util.HashMap;

public class SLRAutomation {

    public final int[] actionTable;

    public final int[] gotoTable;

    private final int stateCount;

    private final int terminalCount;

    private final int nonTerminalCount;

    private final HashMap<Integer, String> nonTerminals = new HashMap<>();

    private final HashMap<Integer, String> terminals = new HashMap<>();

    public SLRAutomation(Grammar grammar) {

        initializeSymbols(grammar);

        LRItems items = new LRItems(grammar);

        stateCount = items.getStateCount();

        nonTerminalCount = grammar.getNonTerminalCount();

        terminalCount = grammar.getTerminalCount() + 1;

        actionTable = new int[terminalCount * stateCount];

        gotoTable = new int[nonTerminalCount * stateCount];

        writeShifts(items);

    }

    private void initializeSymbols(Grammar grammar) {

        for (int nonTerminal : grammar.getNonTerminals()) {

            nonTerminals.put(
                Grammar.nonTerminalToIndex(nonTerminal),
                grammar.nonTerminalToSymbol(nonTerminal)
            );

        }

        for (int terminal : grammar.getTerminals()) {

            terminals.put(
                Grammar.terminalToIndex(terminal),
                grammar.terminalToSymbol(terminal)
            );

        }

        assert !terminals.containsKey(0);

        terminals.put(0, "$");

    }

    private void writeShifts(LRItems items) {

        for (LRItemTransition transition : items.getTransitions()) {

            if (Grammar.symbolIsTerminal(transition.grammarSymbol)) {
                // write to action table

                writeShift(transition.state, transition.grammarSymbol, transition.targetState);

            }
            else {
                // write to goto table

                writeGoto(transition.state, transition.grammarSymbol, transition.targetState);

            }

        }

    }

    private void writeShift(int state, int terminal, int targetState) {
        // TODO: check if the table cell is already occupied
        actionTable[terminalCount * state + Grammar.terminalToIndex(terminal)] = targetState;
    }

    private void writeReduce(int state, int terminal, int productionId) {
        // TODO: check if the table cell is already occupied
        actionTable[terminalCount * state + Grammar.terminalToIndex(terminal)] = encodeProductionId(productionId);
    }

    private void writeGoto(int state, int nonTerminal, int targetState) {
        // TODO: check if the table cell is already occupied
        gotoTable[nonTerminalCount * state + Grammar.nonTerminalToIndex(nonTerminal)] = targetState;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("State count: ");
        sb.append(stateCount);
        sb.append(" ( 0 - ");
        sb.append(stateCount - 1);
        sb.append(" )\n");

        sb.append(String.format("%5s", "STATE"));

        for (int t = 0; t < terminalCount; t++) {

            sb.append(' ');
            sb.append('|');

            String terminal = terminals.get(t);

            sb.append(String.format("%12s", terminal));

        }

        sb.append('\n');

        for (int s = 0; s < stateCount; s++) {

            sb.append(String.format("%4d", s));

            sb.append(' ');

            for (int t = 0; t < terminalCount; t++) {

                sb.append(' ');
                sb.append('|');

                int code = actionTable[s * terminalCount + t];

                if (code == 0) {
                    sb.append(" -----------");
                    continue;
                }

                if (code > 0) {
                    // shift
                    sb.append(String.format("%12s", "s" + code));
                }
                else {
                    sb.append(String.format("%12s", "r" + code));
                }

            }

            sb.append('\n');

        }

        return sb.toString();

    }

    private static int encodeProductionId(int productionId) {
        return -productionId - 1;
    }

    private static int decodeProductionId(int code) {
        return encodeProductionId(code);
    }

}