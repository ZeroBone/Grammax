package net.zerobone.grammax.grammar.slr;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.slr.conflict.Conflict;
import net.zerobone.grammax.grammar.slr.conflict.ReduceAcceptConflict;
import net.zerobone.grammax.grammar.slr.conflict.ReduceReduceConflict;
import net.zerobone.grammax.grammar.slr.conflict.ShiftReduceConflict;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.id.IdSymbol;
import net.zerobone.grammax.grammar.lr.LRItemTransition;
import net.zerobone.grammax.grammar.lr.LRItems;
import net.zerobone.grammax.grammar.utils.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Automation {

    public static final int ACTION_ACCEPT = -1;

    private final int stateCount;

    private final int terminalCount;

    private final int nonTerminalCount;

    public final AutomationProduction[] productions;

    private final String[] nonTerminals;

    private final String[] terminals;

    /**
     * Invariants:
     * 0 => empty (error transition)
     * > 0 => state id
     */
    public final int[] gotoTable;

    /**
     * Invariants:
     * 0 => empty (error transition)
     * -1 => accept
     * < -1 => reduce
     * > 0 => shift
     */
    public final int[] actionTable;

    private final ArrayList<Conflict> conflicts = new ArrayList<>();

    public Automation(Grammar grammar, LRItems items) {

        stateCount = items.getStateCount();

        nonTerminalCount = grammar.getNonTerminalCount();

        terminalCount = grammar.getTerminalCount() + 1;

        // productions

        productions = new AutomationProduction[grammar.getProductionCount()];

        // initialize non-terminals

        nonTerminals = new String[nonTerminalCount];

        for (int nonTerminal : grammar.getNonTerminals()) {

            nonTerminals[Grammar.nonTerminalToIndex(nonTerminal)] = grammar.nonTerminalToSymbol(nonTerminal);

        }

        // initialize terminals

        terminals = new String[terminalCount];

        for (int terminal : grammar.getTerminals()) {

            terminals[Grammar.terminalToIndex(terminal)] = grammar.terminalToSymbol(terminal);

        }

        assert terminals[0] == null;

        terminals[Grammar.TERMINAL_EOF] = "$";

        // initialize tables

        actionTable = new int[terminalCount * stateCount];

        gotoTable = new int[nonTerminalCount * stateCount];

        // fill the tables

        writeShifts(items);

        writeReduces(grammar, items);

    }

    public String nonTerminalToSymbol(int nonTerminalIndex) {
        return nonTerminals[nonTerminalIndex];
    }

    public String terminalToSymbol(int terminalIndex) {
        return terminals[terminalIndex];
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

                    writeReduce(grammar, stateId, terminalOrEof, kernelPoint.productionId);

                }

            }

        }

    }

    // helper methods

    private AutomationProduction convertProduction(IdProduction idProduction) {

        AutomationProduction production = new AutomationProduction(
            Grammar.nonTerminalToIndex(idProduction.getNonTerminal()),
            idProduction.code,
            idProduction.body.size()
        );

        int i = 0;

        for (IdSymbol symbol : idProduction.body) {

            production.body[i++] = new AutomationSymbol(
                symbol.isTerminal(),
                symbol.isTerminal() ? Grammar.terminalToIndex(symbol.id) : Grammar.nonTerminalToIndex(symbol.id),
                symbol.argumentName
            );

        }

        return production;

    }

    private void writeShift(int state, int terminal, int targetState) {

        assert targetState >= 0: "negative target state";
        assert targetState != 0: "initial state cannot have incoming edges";

        int terminalIndex = Grammar.terminalToIndex(terminal);

        if (actionTable[terminalCount * state + terminalIndex] != 0) {

            int code = actionTable[terminalCount * state + terminalIndex];

            assert code != ACTION_ACCEPT;
            assert actionTable[terminalCount * state + terminalIndex] > 0 : "shift-shift conflict cannot occur";

            conflicts.add(new ShiftReduceConflict(
                decodeProductionId(code),
                terminal
            ));

            return;
        }

        actionTable[terminalCount * state + terminalIndex] = targetState;

    }

    private void writeReduce(Grammar grammar, int state, int terminal, int productionId) {

        assert productionId >= 0;

        int terminalIndex = Grammar.terminalToIndex(terminal);

        if (actionTable[terminalCount * state + terminalIndex] != 0) {

            int code = actionTable[terminalCount * state + terminalIndex];

            if (code == ACTION_ACCEPT) {
                // reduce-accept conflict (actually this is a special case of a reduce/reduce conflict)
                conflicts.add(new ReduceAcceptConflict(
                    productionId
                ));
            }
            else if (code < 0) {
                // reduce-reduce conflict
                conflicts.add(new ReduceReduceConflict(
                    decodeProductionId(code),
                    productionId
                ));
            }
            else {
                // code > 0
                // shift-reduce conflict
                conflicts.add(new ShiftReduceConflict(productionId, terminal));
            }

            return;
        }

        productions[productionId] = convertProduction(grammar.getProduction(productionId));

        actionTable[terminalCount * state + terminalIndex] = encodeProductionId(productionId);

    }

    private void writeAccept(int state) {

        if (actionTable[terminalCount * state + Grammar.TERMINAL_EOF] != 0) {
            // TODO
            conflicts.add(null);
            return;
        }

        actionTable[terminalCount * state + Grammar.TERMINAL_EOF] = ACTION_ACCEPT;

    }

    private void writeGoto(int state, int nonTerminal, int targetState) {

        assert targetState >= 0 : "invalid target state.";
        assert targetState != 0 : "there cannot be any transitions to zero state";

        int nonTerminalIndex = Grammar.nonTerminalToIndex(nonTerminal);

        if (gotoTable[nonTerminalCount * state + nonTerminalIndex] != 0) {
            // TODO
            conflicts.add(null);
            return;
        }

        gotoTable[nonTerminalCount * state + nonTerminalIndex] = targetState;

    }

    public ArrayList<Conflict> getConflicts() {
        return conflicts;
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
        sb.append(" (0 - ");
        sb.append(stateCount - 1);
        sb.append(")\n\n");

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