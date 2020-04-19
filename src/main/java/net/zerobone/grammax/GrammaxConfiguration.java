package net.zerobone.grammax;

import net.zerobone.grammax.grammar.automation.Automation;

import java.util.HashMap;

public class GrammaxConfiguration {

    public final Automation automation;

    public final ParsingAlgorithm algorithm;

    public final String topCode;

    private final String name;

    private final HashMap<String, String> typeMap;

    public GrammaxConfiguration(Automation automation, ParsingAlgorithm algorithm, String topCode, String name, HashMap<String, String> typeMap) {
        this.automation = automation;
        this.algorithm = algorithm;
        this.topCode = topCode;
        this.name = name;
        this.typeMap = typeMap;
    }

    public String getTypeForSymbol(String symbol) {
        return typeMap.get(symbol);
    }

    public String getName() {
        return name == null ? "Parser" : name;
    }

}