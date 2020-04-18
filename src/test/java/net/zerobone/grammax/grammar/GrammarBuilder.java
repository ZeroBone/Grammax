package net.zerobone.grammax.grammar;

import net.zerobone.grammax.utils.BijectiveMap;
import org.junit.jupiter.api.Assertions;

public class GrammarBuilder {

    private Grammar grammar = null;

    private BijectiveMap<Integer, Integer> idToNumber = new BijectiveMap<>();

    public GrammarBuilder() {}

    public void production(int number, String label, String... body) {

        Assertions.assertFalse(GrammarUtils.stringIsTerminal(label));

        Production production = GrammarUtils.createProduction(body);

        if (grammar == null) {
            grammar = new Grammar(label, production);
        }
        else {
            grammar.addProduction(label, production);
        }

        assert production.getId() != Production.ID_INVALID;

        // verify there are now duplicate production ids
        Assertions.assertFalse(idToNumber.containsKey(production.getId()));

        // verify there are now duplicate number ids
        Assertions.assertFalse(idToNumber.containsValue(number));

        idToNumber.put(production.getId(), number);

    }

    public Grammar getGrammar() {
        return grammar;
    }

    public BijectiveMap<Integer, Integer> getIdToNumber() {
        return idToNumber;
    }

}