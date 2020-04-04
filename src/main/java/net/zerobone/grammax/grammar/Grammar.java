package net.zerobone.grammax.grammar;

import net.zerobone.grammax.grammar.id.IdGrammar;
import net.zerobone.grammax.grammar.id.IdProduction;
import net.zerobone.grammax.grammar.id.IdSymbol;
import net.zerobone.grammax.utils.BijectiveMap;

import java.util.*;

public class Grammar extends IdGrammar {

    private static final int START_SYMBOL_ID = -1;

    private int nonTerminalCounter = -2;

    private int terminalCounter = 1;

    private BijectiveMap<String, Integer> nonTerminals = new BijectiveMap<>();

    private BijectiveMap<String, Integer> terminals = new BijectiveMap<>();

    public Grammar(String startSymbol, Production startProduction) {

        super(START_SYMBOL_ID);

        nonTerminals.put(startSymbol, START_SYMBOL_ID);

        createFirstProduction(START_SYMBOL_ID, convertProduction(startProduction));

    }

    public boolean symbolDefined(String symbol) {
        return nonTerminals.containsKey(symbol) || terminals.containsKey(symbol);
    }

    public String idToSymbol(int id) {

        assert id != FOLLOW_SET_EOF : "eof-to-symbol convertion is restricted";

        if (id < 0) {
            // non-terminal
            assert nonTerminals.mapValue(id) != null : "Non-terminal " + id + " is not defined";
            return nonTerminals.mapValue(id);
        }

        // terminal

        assert terminals.mapValue(id) != null : "Terminal " + id + " is not defined";
        return terminals.mapValue(id);

    }

    private Integer symbolToId(Symbol symbol) {

        if (symbol.isTerminal) {
            return terminals.mapKey(symbol.id);
        }

        return nonTerminals.mapKey(symbol.id);

    }

    private IdSymbol convertSymbol(Symbol symbol) {

        Integer symbolId = symbolToId(symbol);

        if (symbolId == null) {

            if (symbol.isTerminal) {

                symbolId = terminalCounter;

                terminals.put(symbol.id, terminalCounter);

                terminalCounter++;

            }
            else {

                symbolId = nonTerminalCounter;

                nonTerminals.put(symbol.id, nonTerminalCounter);

                nonTerminalCounter--;

            }

        }

        return new IdSymbol(symbolId, symbol.argumentName);

    }

    private IdProduction convertProduction(Production production) {

        IdProduction idProduction = new IdProduction(production.getCode());

        for (Symbol symbol : production.getBody()) {
            idProduction.body.add(convertSymbol(symbol));
        }

        return idProduction;

    }

    public void addProduction(String symbol, Production production) {

        Integer symbolId = nonTerminals.mapKey(symbol);

        if (symbolId == null) {

            // no such symbol

            nonTerminals.put(symbol, nonTerminalCounter);

            createFirstProduction(nonTerminalCounter, convertProduction(production));

            nonTerminalCounter--;

            return;

        }

        // symbol already exists
        // but it doesn't mean the production exists

        ArrayList<Integer> correspondingProductions = productionMap.get(symbolId);

        if (correspondingProductions == null) {
            createFirstProduction(symbolId, convertProduction(production));
            return;
        }

        // add to existing production

        IdProduction convertedProduction = convertProduction(production);

        assert convertedProduction.getId() == 0;

        // assign an id to the production
        createProduction(convertedProduction);

        assert convertedProduction.getId() != 0;

        correspondingProductions.add(convertedProduction.getId());

    }

    int createNonTerminal(int analogyNonTerminal) {

        String newSymbol = idToSymbol(analogyNonTerminal) + "'";

        if (nonTerminals.containsKey(newSymbol)) {

            StringBuilder sb = new StringBuilder(newSymbol);

            do {
                sb.append('\'');
                newSymbol = sb.toString();
            } while (nonTerminals.containsKey(newSymbol));

        }

        nonTerminals.put(newSymbol, nonTerminalCounter);

        return nonTerminalCounter--;

    }

    void addProduction(int symbolId, IdProduction production) {

        assert production.getId() == 0;

        ArrayList<Integer> correspondingProductions = productionMap.get(symbolId);

        if (correspondingProductions == null) {

            createFirstProduction(symbolId, production);

            return;

        }

        // assign an id to the production
        createProduction(production);

        assert production.getId() != 0;

        correspondingProductions.add(production.getId());

    }

    public int getTerminalCount() {
        return terminals.size();
    }

    public int getNonTerminalCount() {
        return nonTerminals.size();
    }

    public String toString(boolean debug) {

        StringBuilder sb = new StringBuilder();

        Iterator<Map.Entry<Integer, ArrayList<Integer>>> it = productionMap.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry<Integer, ArrayList<Integer>> pair = it.next();

            String label = (debug ? "(" + pair.getKey() + ") " : "") + idToSymbol(pair.getKey());

            sb.append(label);
            sb.append(" -> ");

            Iterator<Integer> productionIterator = pair.getValue().iterator();

            // assert productionIterator.hasNext();

            if (productionIterator.hasNext()) {
                while (true) {

                    IdProduction ip = getProduction(productionIterator.next());

                    sb.append(ip.toString(this));

                    if (!productionIterator.hasNext()) {
                        break;
                    }

                    sb.append('\n');
                    for (int i = 0; i < label.length(); i++) {
                        sb.append(' ');
                    }
                    sb.append("  | ");

                }
            }

            sb.append(';');

            if (it.hasNext()) {
                sb.append('\n');
            }

        }

        return sb.toString();

    }

    @Override
    public String toString() {
        return toString(false);
    }

}