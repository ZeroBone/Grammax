package net.zerobone.grammax.grammar.id;

public class IdSymbol {

    int id;

    public final String argumentName;

    public IdSymbol(int id, String argumentName) {
        this.id = id;
        this.argumentName = argumentName;
    }

    boolean isTerminal() {
        return id > 0;
    }

}