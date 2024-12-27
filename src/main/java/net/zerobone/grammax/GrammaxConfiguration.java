package net.zerobone.grammax;

import net.zerobone.grammax.grammar.automation.Automation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GrammaxConfiguration {

    public final Automation automation;

    public final ParsingAlgorithm algorithm;

    public final TargetLanguage targetLanguage;

    public final String topCode;

    private final String name;

    private final HashMap<String, String> typeMap;

    public GrammaxConfiguration(Automation automation, ParsingAlgorithm algorithm, TargetLanguage targetLanguage, String topCode, String name, HashMap<String, String> typeMap) {
        this.automation = automation;
        this.algorithm = algorithm;
        this.targetLanguage = targetLanguage;
        this.topCode = topCode;
        this.name = name;
        this.typeMap = typeMap;
    }

    public Map<String, String> getSymbolToTypeMap() {
        return Collections.unmodifiableMap(typeMap);
    }

    public String getTypeForSymbol(String symbol) {
        return typeMap.get(symbol);
    }

    public String getName() {
        return name == null ? "Parser" : name;
    }

}