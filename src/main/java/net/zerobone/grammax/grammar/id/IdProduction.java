package net.zerobone.grammax.grammar.id;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.utils.zerolist.ZeroListable;

import java.util.ArrayList;
import java.util.Iterator;

public class IdProduction implements ZeroListable {

    private int nonTerminal = 0;

    private int id = 0;

    public String code;

    public ArrayList<IdSymbol> body = new ArrayList<>();

    public IdProduction(String code) {
        this.code = code;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String toString(Grammar grammar) {

        StringBuilder sb = new StringBuilder();

        Iterator<IdSymbol> it = body.iterator();

        while (it.hasNext()) {

            IdSymbol symbol = it.next();

            sb.append(grammar.idToSymbol(symbol.id));

            if (symbol.argumentName != null) {

                sb.append('(');
                sb.append(symbol.argumentName);
                sb.append(')');

            }

            if (it.hasNext()) {
                sb.append(' ');
            }

        }

        if (code != null) {

            sb.append("    ");
            sb.append("{ ");
            sb.append(code.replace('\n', ' '));
            sb.append(" }");

        }

        return sb.toString();

    }

    public int getNonTerminal() {
        return nonTerminal;
    }

    public void setNonTerminal(int nonTerminal) {
        this.nonTerminal = nonTerminal;
    }

}