package net.zerobone.grammax.grammar.slr;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.GrammarBuilder;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.automation.Automation;
import net.zerobone.grammax.grammar.lr0.LR0Items;
import net.zerobone.grammax.utils.BijectiveMap;
import org.junit.jupiter.api.Assertions;

public class SLRTestCase {

    private final Automation automation;

    private final BijectiveMap<Integer, Integer> productionIdToNumber;

    private final BijectiveMap<String, Integer> terminalToId = new BijectiveMap<>();

    private final BijectiveMap<String, Integer> nonTerminalToId = new BijectiveMap<>();

    private final int[] actionTable;

    private final int[] gotoTable;

    public SLRTestCase(GrammarBuilder grammarBuilder) {

        Grammar grammar = grammarBuilder.getGrammar();

        productionIdToNumber = grammarBuilder.getIdToNumber();

        assert grammar != null;

        grammar.augment();

        LR0Items items = new LR0Items(grammar);

        automation = new SLRAutomation(grammar, items);

        actionTable = new int[automation.actionTable.length];

        gotoTable = new int[automation.gotoTable.length];

    }

    private void verifyActionTableCell(int nonTerminal, int state, String expected) {

        int terminalCount = automation.getTerminalCount();

        int code = automation.actionTable[state * terminalCount + nonTerminal];

        if (code == 0) {
            Assertions.assertTrue(expected.isEmpty());
            return;
        }

        if (code == Automation.ACTION_ACCEPT) {
            Assertions.assertEquals("accept", expected);
            return;
        }

        if (code > 0) {
            // shift action

            int stateId = Automation.decodeTargetState(code);

            Assertions.assertTrue(expected.startsWith("s"));

        }

    }

    public void expectAction(int state, String terminal, String expected) {

        assert !terminal.isEmpty();

        if (terminal.equals("$")) {
            terminal = Symbol.EOF.id;
        }

        int terminalIndex = automation.symbolToTerminalIndex(terminal);

        if (state == 0) {

        }

        // TODO

    }

    public void test() {

        Assertions.fail("oh");

    }

}