package net.zerobone.grammax;

import net.zerobone.grammax.ast.entities.ProductionSymbol;
import net.zerobone.grammax.ast.statements.ProductionStatementNode;
import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.Symbol;

import java.util.ArrayList;
import java.util.HashMap;

public class GrammaxContext {

    public Grammar grammar = null;

    private String imports = null;

    private final HashMap<String, String> typeMap = new HashMap<>();

    private final ArrayList<String> errors = new ArrayList<>();

    public void addProduction(String nonTerminal, ProductionStatementNode production) {

        if (grammar == null) {
            grammar = new Grammar(nonTerminal, convertProduction(production));
        }
        else {
            grammar.addProduction(nonTerminal, convertProduction(production));
        }

    }

    private static Production convertProduction(ProductionStatementNode statement) {

        Production production = new Production(statement.code);

        for (ProductionSymbol symbol : statement.production) {
            production.append(new Symbol(symbol.id, symbol.terminal, symbol.argument));
        }

        return production;

    }

    public void addType(String symbol, String type) {

        if (typeMap.containsKey(symbol)) {
            addError("Duplicate type declaration for symbol '" + symbol + "'.");
            return;
        }

        typeMap.put(symbol, type);

    }

    private void addError(String error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return grammar == null || !errors.isEmpty();
    }

    private static void printError(String error) {
        System.err.print("[ERR]: ");
        System.err.println(error);
    }

    public void printErrors() {

        if (grammar == null) {
            printError("Could not find the starting symbol.");
        }

        for (String error : errors) {
            printError(error);
        }

    }

    public GrammaxConfiguration getConfiguration() {
        return new GrammaxConfiguration(imports, typeMap);
    }

    public void setImports(String imports) {

        if (this.imports != null) {
            addError("Duplicate imports statement.");
            return;
        }

        this.imports = imports;

    }

}