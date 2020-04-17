package net.zerobone.grammax.grammar;

public class ProductionSymbol {

    public Symbol symbol;

    public final String argumentName;

    public ProductionSymbol(Symbol symbol, String argumentName) {
        this.symbol = symbol;
        this.argumentName = argumentName;
    }

    public ProductionSymbol(String symbol, boolean isTerminal, String argumentName) {
        this.symbol = new Symbol(symbol, isTerminal);
        this.argumentName = argumentName;
    }

}