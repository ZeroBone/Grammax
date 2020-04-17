package net.zerobone.grammax.grammar.verification.messages;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Symbol;

import java.util.Iterator;

public abstract class VerificationMessage {

    public final boolean warning;

    public VerificationMessage(boolean warning) {
        this.warning = warning;
    }

    public abstract String getMessage(Grammar grammar);

    protected static void appendListOfNonterminals(Iterator<Symbol> nonTerminalIterator, StringBuilder sb) {

        assert nonTerminalIterator.hasNext();

        while (true) {

            Symbol nonTerminal = nonTerminalIterator.next();
            assert !nonTerminal.isTerminal;

            sb.append('\'');
            sb.append(nonTerminal.id);
            sb.append('\'');

            if (!nonTerminalIterator.hasNext()) {
                break;
            }

            sb.append(", ");

        }

    }

}