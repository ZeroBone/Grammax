package net.zerobone.grammax.grammar;

import org.junit.jupiter.api.Assertions;

public class GrammarUtils {

    private GrammarUtils() {}

    public static boolean stringIsTerminal(String str) {
        return Character.isUpperCase(str.charAt(0));
    }

    public static Production createProduction(String... symbols) {

        Production production = new Production(null);

        Assertions.assertTrue(production.body.isEmpty());

        for (String str : symbols) {

            production.body.add(new ProductionSymbol(str, stringIsTerminal(str), null));

        }

        Assertions.assertEquals(production.body.size(), symbols.length);

        return production;

    }

}