package net.zerobone.grammax;

import java.util.HashMap;

public class GrammaxConfiguration {

    public final String topCode;

    private final String name;

    private final HashMap<String, String> typeMap;

    public GrammaxConfiguration(String topCode, String name, HashMap<String, String> typeMap) {
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