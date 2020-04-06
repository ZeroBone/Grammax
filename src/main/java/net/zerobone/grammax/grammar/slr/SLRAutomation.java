package net.zerobone.grammax.grammar.slr;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.lr.LRItemTransition;
import net.zerobone.grammax.grammar.lr.LRItems;
import net.zerobone.grammax.grammar.utils.Point;

import java.util.HashMap;
import java.util.HashSet;

public class SLRAutomation {

    public static final int ACTION_ACCEPT = -1;

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

        // fill the tables

        writeShifts(items);

        writeReduces(grammar, items);

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

    private void writeReduces(Grammar grammar, LRItems items) {

        for (HashMap.Entry<HashSet<Point>, Integer> entry : items.getStates()) {

            HashSet<Point> derivative = entry.getKey();

            int stateId = entry.getValue();

            for (Point kernelPoint : derivative) {

                IdProduction pointProduction = grammar.getProduction(kernelPoint.productionId);

                assert kernelPoint.position <= pointProduction.body.size();

                if (kernelPoint.position != pointProduction.body.size()) {
                    // if the point is not at the end of the production
                    continue;
                }

                int nonTerminal = grammar.getProduction(kernelPoint.productionId).getNonTerminal();

                if (nonTerminal == grammar.getStartSymbol()) {

                    writeAccept(stateId);

                    continue;
                }

                System.out.println("[LOG]: Ending point for label '" + grammar.nonTerminalToSymbol(nonTerminal) + "' found in state " + stateId);
                // compute the follow set of the label of the production with the point at the end

                for (int terminalOrEof : grammar.followSet(nonTerminal)) {

                    writeReduce(stateId, terminalOrEof, kernelPoint.productionId);

                }

            }

        }

    }

    // helper methods

    private void writeShift(int state, int terminal, int targetState) {
        // TODO: check if the table cell is already occupied
        actionTable[terminalCount * state + Grammar.terminalToIndex(terminal)] = targetState;
    }

    private void writeReduce(int state, int terminal, int productionId) {
        // TODO: check if the table cell is already occupied
        actionTable[terminalCount * state + Grammar.terminalToIndex(terminal)] = encodeProductionId(productionId);
    }

    private void writeAccept(int state) {
        // TODO: check if the table cell is already occupied
        actionTable[terminalCount * state + Grammar.TERMINAL_EOF] = ACTION_ACCEPT;
    }

    private void writeGoto(int state, int nonTerminal, int targetState) {
        // TODO: check if the table cell is already occupied
        gotoTable[nonTerminalCount * state + Grammar.nonTerminalToIndex(nonTerminal)] = targetState;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Non-terminal count: ");
        sb.append(nonTerminalCount);
        sb.append('\n');

        sb.append("Terminal count: ");
        sb.append(terminalCount);
        sb.append('\n');

        sb.append("State count: ");
        sb.append(stateCount);
        sb.append(" ( 0 - ");
        sb.append(stateCount - 1);
        sb.append(" )\n\n");

        // action table

        sb.append("Action table:\n");

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

                if (code == ACTION_ACCEPT) {
                    sb.append(String.format("%12s", "accept"));
                    continue;
                }

                if (code > 0) {
                    // shift
                    sb.append(String.format("%12s", "s" + code));
                }
                else {
                    sb.append(String.format("%12s", "r" + decodeProductionId(code)));
                }

            }

            sb.append('\n');

        }

        // goto table

        sb.append("\n\nGoto table:\n");

        sb.append(String.format("%5s", "STATE"));

        for (int nt = 0; nt < nonTerminalCount; nt++) {

            sb.append(" |");

            String nonTerminal = nonTerminals.get(nt);

            sb.append(String.format("%12s", nonTerminal));

        }

        sb.append('\n');

        for (int s = 0; s < stateCount; s++) {

            sb.append(String.format("%4d", s));

            sb.append(' ');

            for (int t = 0; t < nonTerminalCount; t++) {

                sb.append(' ');
                sb.append('|');

                int entry = gotoTable[s * nonTerminalCount + t];

                if (entry == 0) {
                    sb.append(" -----------");
                    continue;
                }

                sb.append(String.format("%12s", entry));

            }

            sb.append('\n');

        }

        return sb.toString();

    }

    private static int encodeProductionId(int productionId) {
        return -productionId - 2;
    }

    private static int decodeProductionId(int code) {
        return encodeProductionId(code);
    }

}