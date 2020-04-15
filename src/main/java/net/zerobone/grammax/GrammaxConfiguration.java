package net.zerobone.grammax;

import java.util.HashMap;

public class GrammaxConfiguration {

    public final String topCode;

    private final HashMap<String, String> typeMap;

    public GrammaxConfiguration(String topCode, HashMap<String, String> typeMap) {
        this.topCode = topCode;
        this.typeMap = typeMap;
    }

    public String getTypeForSymbol(String symbol) {
        return typeMap.get(symbol);
    }

}