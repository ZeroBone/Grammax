package net.zerobone.grammax.grammar.verification.messages;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Symbol;

import java.util.ArrayList;
import java.util.Iterator;

public class RightRecursiveCycleMessage extends VerificationMessage {

    public final ArrayList<Symbol> cycle;

    public RightRecursiveCycleMessage(ArrayList<Symbol> cycle) {
        super(true);
        this.cycle = cycle;
    }

    @Override
    public String getMessage(Grammar grammar) {

        StringBuilder sb = new StringBuilder();

        sb.append("Non-terminal(s) ");

        Iterator<Symbol> cycleIterator = cycle.iterator();

        appendListOfNonterminals(cycleIterator, sb);

        sb.append(" form a right-recursive cycle.");

        return sb.toString();

    }

}