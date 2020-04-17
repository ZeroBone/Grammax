package net.zerobone.grammax.grammar.verification.messages;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Symbol;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UnreachableNonTerminalsMessage extends VerificationMessage {

    private final Set<Symbol> unreachableNonTerminals;

    public UnreachableNonTerminalsMessage(HashSet<Symbol> unreachableNonTerminals) {
        super(false);
        this.unreachableNonTerminals = unreachableNonTerminals;
    }

    @Override
    public String getMessage(Grammar grammar) {

        StringBuilder sb = new StringBuilder();

        sb.append("Non-terminal(s) ");

        Iterator<Symbol> nonTerminalIterator = unreachableNonTerminals.iterator();

        assert nonTerminalIterator.hasNext();

        appendListOfNonterminals(nonTerminalIterator, sb);

        sb.append(" cannot be reached from the start symbol '");
        sb.append(grammar.getStartSymbol().id);
        sb.append("'.");

        return sb.toString();

    }

}