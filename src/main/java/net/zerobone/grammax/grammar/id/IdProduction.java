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

        if (it.hasNext()) {
            while (true) {

                IdSymbol symbol = it.next();

                sb.append(grammar.idToSymbol(symbol.id));

                if (symbol.argumentName != null) {

                    sb.append('(');
                    sb.append(symbol.argumentName);
                    sb.append(')');

                }

                if (it.hasNext()) {
                    sb.append(' ');
                    continue;
                }

                break;

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

    public String stringifyWithPointMarker(Grammar grammar, int pointPosition) {

        StringBuilder sb = new StringBuilder();

        sb.append(grammar.nonTerminalToSymbol(nonTerminal));

        sb.append(" -> ");

        if (!body.isEmpty()) {

            for (int i = 0;; i++) {

                if (i == pointPosition) {
                    sb.append("* ");
                }

                IdSymbol symbol = body.get(i);

                sb.append(grammar.idToSymbol(symbol.id));

                if (i != body.size() - 1) {
                    assert i < body.size() - 1;
                    sb.append(' ');
                    continue;
                }

                break;

            }

        }

        if (body.size() == pointPosition) {
            sb.append(" *");
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