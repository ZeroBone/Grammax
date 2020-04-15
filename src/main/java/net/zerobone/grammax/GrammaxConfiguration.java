package net.zerobone.grammax;

import java.util.HashMap;

public class GrammaxConfiguration {

    public final String imports;

    private final HashMap<String, String> typeMap;

    public GrammaxConfiguration(String imports, HashMap<String, String> typeMap) {
        this.imports = imports;
        this.typeMap = typeMap;
    }

    public String getTypeForSymbol(String symbol) {
        return typeMap.get(symbol);
    }

}