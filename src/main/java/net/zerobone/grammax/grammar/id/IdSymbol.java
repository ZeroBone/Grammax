package net.zerobone.grammax.grammar.id;

public class IdSymbol {

    public final int id;

    public final String argumentName;

    public IdSymbol(int id, String argumentName) {
        this.id = id;
        this.argumentName = argumentName;
    }

    public boolean isTerminal() {
        return id > 0;
    }

}