package net.zerobone.grammax.grammar.verification.messages;

import net.zerobone.grammax.grammar.Grammar;

import java.util.Iterator;
import java.util.LinkedList;

public class RightRecursiveCycleMessage extends VerificationMessage {

    public final LinkedList<Integer> cycle;

    public RightRecursiveCycleMessage(LinkedList<Integer> cycle) {
        super(true);
        this.cycle = cycle;
    }

    @Override
    public String getMessage(Grammar grammar) {

        StringBuilder sb = new StringBuilder();

        sb.append("Non-terminal(s) ");

        Iterator<Integer> cycleIterator = cycle.iterator();

        appendListOfNonterminals(cycleIterator, sb, grammar);

        sb.append(" form a right-recursive cycle.");

        return sb.toString();

    }

}