package net.zerobone.grammax.ast.entities;

import java.util.ArrayList;

public class ProductionStatementBody {

    private ArrayList<ProductionSymbol> production = new ArrayList<>();

    public ProductionStatementBody() {}

    public void addNonTerminal(String id) {
        production.add(new ProductionSymbol(id, false));
    }

    public void addNonTerminal(String id, String argument) {
        production.add(new ProductionSymbol(id, argument,false));
    }

    public void addTerminal(String id) {
        production.add(new ProductionSymbol(id, true));
    }

    public void addTerminal(String id, String argument) {
        production.add(new ProductionSymbol(id, argument,true));
    }

    public ArrayList<ProductionSymbol> getProduction() {
        return production;
    }

}