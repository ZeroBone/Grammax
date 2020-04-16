package net.zerobone.grammax.grammar.verification.messages;

import net.zerobone.grammax.grammar.Grammar;

import java.util.Iterator;
import java.util.Set;

public class UnreachableNonTerminalsMessage extends VerificationMessage {

    private final Set<Integer> unreachableNonTerminals;

    public UnreachableNonTerminalsMessage(Set<Integer> unreachableNonTerminals) {
        super(false);
        this.unreachableNonTerminals = unreachableNonTerminals;
    }

    @Override
    public String getMessage(Grammar grammar) {

        StringBuilder sb = new StringBuilder();

        sb.append("Non-terminal(s) ");

        Iterator<Integer> nonTerminalIterator = unreachableNonTerminals.iterator();

        assert nonTerminalIterator.hasNext();

        appendListOfNonterminals(nonTerminalIterator, sb, grammar);

        sb.append(" cannot be reached from the start symbol '");
        sb.append(grammar.idToSymbol(grammar.getStartSymbol()));
        sb.append("'.");

        return sb.toString();

    }

}