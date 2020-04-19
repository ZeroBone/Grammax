package net.zerobone.grammax.grammar;

import java.util.HashSet;
import java.util.Iterator;

public final class Symbol {

    // used in first and follow sets
    public static final Symbol EPSILON = new Symbol("", true);

    // used in follow sets
    public static final Symbol EOF = new Symbol("EOF", true);

    public final String id;

    public final boolean isTerminal;

    Symbol(String id, boolean isTerminal) {
        this.id = id;
        this.isTerminal = isTerminal;
    }

    public static boolean isSpecial(Symbol symbol) {
        return symbol == EPSILON || symbol == EOF;
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }

    public static String prettyPrintSet(HashSet<Symbol> symbols) {

        Iterator<Symbol> symbolIterator = symbols.iterator();

        if (!symbolIterator.hasNext()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (;;) {

            Symbol next = symbolIterator.next();

            sb.append(next.id);

            if (!symbolIterator.hasNext()) {
                break;
            }

            sb.append(", ");

        }

        sb.append('}');

        return sb.toString();

    }

}