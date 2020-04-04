package net.zerobone.grammax.grammar.id;

import net.zerobone.grammax.utils.zerolist.ZeroList;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class IdGrammar {

    // terminals are represented as positive integers, so this one should be negative
    public static final int FIRST_FOLLOW_SET_EPSILON = -1; // aka empty string

    public static final int FOLLOW_SET_EOF = 0; // aka dollar sign

    protected HashMap<Integer, ArrayList<Integer>> productionMap = new HashMap<>();

    private ZeroList<IdProduction> productions = new ZeroList<>();

    private int startSymbol;

    public IdGrammar(int startSymbol) {
        this.startSymbol = startSymbol;
    }

    protected void createFirstProduction(int nonTerminalId, IdProduction production) {

        productions.add(production);

        ArrayList<Integer> createdProductions = new ArrayList<>();

        createdProductions.add(production.getId());

        productionMap.put(nonTerminalId, createdProductions);

    }

    protected void createProduction(IdProduction production) {
        productions.add(production);
    }

    public void setStartSymbol(int startSymbol) {
        this.startSymbol = startSymbol;
    }

    protected IdProduction getProduction(int productionId) {
        return productions.get(productionId);
    }

}