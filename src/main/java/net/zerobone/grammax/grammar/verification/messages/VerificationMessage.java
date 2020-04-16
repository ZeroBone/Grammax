package net.zerobone.grammax.grammar.verification.messages;

import net.zerobone.grammax.grammar.Grammar;

import java.util.Iterator;

public abstract class VerificationMessage {

    public final boolean warning;

    public VerificationMessage(boolean warning) {
        this.warning = warning;
    }

    public abstract String getMessage(Grammar grammar);

    protected static void appendListOfNonterminals(Iterator<Integer> nonTerminalIterator, StringBuilder sb, Grammar grammar) {

        assert nonTerminalIterator.hasNext();

        while (true) {

            int nonTerminal = nonTerminalIterator.next();

            sb.append('\'');
            sb.append(grammar.idToSymbol(nonTerminal));
            sb.append('\'');

            if (!nonTerminalIterator.hasNext()) {
                break;
            }

            sb.append(", ");

        }

    }

}