package net.zerobone.grammax;

import java.util.HashMap;

public class GrammaxConfiguration {

    private final HashMap<String, String> typeMap;

    public GrammaxConfiguration(HashMap<String, String> typeMap) {
        this.typeMap = typeMap;
    }

    public String getTypeForSymbol(String symbol) {
        return typeMap.get(symbol);
    }

}