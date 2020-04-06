package net.zerobone.grammax.grammar.id;

import net.zerobone.grammax.grammar.utils.Point;
import net.zerobone.grammax.utils.zerolist.ZeroList;

import java.util.*;

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

    protected void createFirstProduction(int nonTerminal, IdProduction production) {

        productions.add(production);

        ArrayList<Integer> createdProductions = new ArrayList<>();

        createdProductions.add(production.getId());

        productionMap.put(nonTerminal, createdProductions);

    }

    protected void createProduction(IdProduction production) {
        productions.add(production);
    }

    public void setStartSymbol(int startSymbol) {
        this.startSymbol = startSymbol;
    }

    public int getStartSymbol() {
        return startSymbol;
    }

    public IdSymbol getSymbolAfter(Point point) {

        IdProduction production = getProduction(point.productionId);

        assert production != null;

        try {
            return production.body.get(point.position);
        }
        catch (IndexOutOfBoundsException ignored) {
            return null;
        }

    }

    public IdProduction getProduction(int productionId) {
        return productions.get(productionId);
    }

    public Set<Map.Entry<Integer, ArrayList<Integer>>> getProductions() {
        return productionMap.entrySet();
    }

    public Collection<ArrayList<Integer>> getProductionIds() {
        return productionMap.values();
    }

    public ArrayList<Integer> getProductionsFor(int nonTerminal) {
        return productionMap.get(nonTerminal);
    }

}