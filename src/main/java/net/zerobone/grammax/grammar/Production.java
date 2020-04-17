package net.zerobone.grammax.grammar;

import net.zerobone.grammax.utils.zerolist.ZeroListable;

import java.util.ArrayList;
import java.util.Iterator;

public class Production implements ZeroListable {

    public static final int ID_INVALID = -1;

    private Symbol nonTerminal = null;

    private int id = ID_INVALID;

    public final String code;

    public ArrayList<ProductionSymbol> body = new ArrayList<>();

    public Production(String code) {
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

    public Symbol getNonTerminal() {
        return nonTerminal;
    }

    void setNonTerminal(Symbol nonTerminal) {
        this.nonTerminal = nonTerminal;
    }

    public String stringifyWithPointMarker(int pointPosition) {

        assert pointPosition <= body.size() : "position out of bounds";
        assert nonTerminal != null : "this productions doesn't belong to any grammar";
        assert id != ID_INVALID;

        StringBuilder sb = new StringBuilder();

        sb.append(nonTerminal.id);

        sb.append(" -> ");

        if (!body.isEmpty()) {

            for (int i = 0;; i++) {

                if (i == pointPosition) {
                    sb.append("* ");
                }

                ProductionSymbol symbol = body.get(i);

                sb.append(symbol.symbol.id);

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

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        Iterator<ProductionSymbol> it = body.iterator();

        if (it.hasNext()) {
            while (true) {

                ProductionSymbol symbol = it.next();

                sb.append(symbol.symbol.id);

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

}