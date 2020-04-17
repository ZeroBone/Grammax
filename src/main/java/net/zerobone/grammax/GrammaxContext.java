package net.zerobone.grammax;

import net.zerobone.grammax.ast.statements.ProductionStatementNode;
import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.ProductionSymbol;
import net.zerobone.grammax.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class GrammaxContext {

    public Grammar grammar = null;

    private String topCode = null;

    private String name = null;

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

        for (net.zerobone.grammax.ast.entities.ProductionSymbol symbol : statement.production) {
            production.body.add(new ProductionSymbol(symbol.id, symbol.terminal, symbol.argument));
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
        return new GrammaxConfiguration(topCode, name, typeMap);
    }

    public void setTopCode(String topCode) {

        if (this.topCode != null) {
            addError("Duplicate %top statement.");
            return;
        }

        this.topCode = topCode;

    }

    public void setName(String name) {

        if (this.name != null) {
            addError("Duplicate %name statement.");
            return;
        }

        assert !name.isEmpty();

        if (!StringUtils.startsWithCapital(name)) {
            addError("The name of the class should start with a capital letter.");
            return;
        }

        if (name.length() > 20) {
            addError("The name of the class is too long.");
            return;
        }

        this.name = name;

    }

}