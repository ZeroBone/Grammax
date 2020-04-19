package net.zerobone.grammax.grammar;

import net.zerobone.grammax.grammar.point.Point;
import net.zerobone.grammax.grammar.utils.*;
import net.zerobone.grammax.utils.zerolist.ZeroList;

import java.util.*;

public class Grammar implements Iterable<Production> {

    private HashMap<String, Symbol> nonTerminals = new HashMap<>();

    private HashMap<String, Symbol> terminals = new HashMap<>();

    private HashMap<Symbol, ArrayList<Integer>> productionMap = new HashMap<>();

    private ZeroList<Production> productions = new ZeroList<>();

    private Symbol startSymbol;

    private HashMap<Symbol, HashSet<Symbol>> cachedFirstSets = null;

    private HashMap<Symbol, HashSet<Symbol>> cachedFollowSets = null;

    public Grammar(String startSymbol, Production startProduction) {

        this.startSymbol = new Symbol(startSymbol, false);

        nonTerminals.put(startSymbol, this.startSymbol);

        initializeProduction(this.startSymbol, startProduction);

        createFirstProduction(startProduction);

    }

    private void initializeProduction(Symbol symbol, Production production) {

        assert symbol != null : "symbol cannot be null";
        assert !Symbol.isSpecial(symbol) : "symbol cannot be special";
        assert production.getNonTerminal() == null : "production was already initialized";
        assert production.getId() == Production.ID_INVALID : "production was already initialized by zerolist";

        production.setNonTerminal(symbol);

        for (ProductionSymbol productionSymbol : production.body) {

            if (productionSymbol.symbol.isTerminal) {
                // terminal symbol

                Symbol registeredTerminal = terminals.get(productionSymbol.symbol.id);

                if (registeredTerminal == null) {
                    // register new terminal symbol
                    terminals.put(productionSymbol.symbol.id, productionSymbol.symbol);
                    continue;
                }

                // references of symbols must always lead to one of the hashmap values
                productionSymbol.symbol = registeredTerminal;

                continue;

            }

            // non-terminal

            Symbol registeredNonTerminal = nonTerminals.get(productionSymbol.symbol.id);

            if (registeredNonTerminal == null) {
                // register new terminal symbol
                nonTerminals.put(productionSymbol.symbol.id, productionSymbol.symbol);
                continue;
            }

            // references of symbols must always lead to one of the hashmap values
            productionSymbol.symbol = registeredNonTerminal;

        }

    }

    private void createFirstProduction(Production production) {

        assert production != null;
        assert production.getId() == Production.ID_INVALID : "production is already registered";
        assert production.getNonTerminal() != null : "production is not initialized";

        productions.add(production);

        assert production.getId() != Production.ID_INVALID : "production should be now registered";

        ArrayList<Integer> createdProductions = new ArrayList<>();

        createdProductions.add(production.getId());

        productionMap.put(production.getNonTerminal(), createdProductions);

    }

    public void addProduction(String symbolString, Production production) {

        Symbol symbol = nonTerminals.get(symbolString);

        if (symbol == null) {

            // no such symbol

            Symbol newSymbol = new Symbol(symbolString, false);

            nonTerminals.put(symbolString, newSymbol);

            initializeProduction(newSymbol, production);

            createFirstProduction(production);

            return;

        }

        // symbol already exists
        // but it doesn't mean the production exists

        ArrayList<Integer> correspondingProductions = productionMap.get(symbol);

        if (correspondingProductions == null) {
            initializeProduction(symbol, production);
            createFirstProduction(production);
            return;
        }

        // add to existing production

        initializeProduction(symbol, production);

        assert production.getId() == Production.ID_INVALID;

        // assign an id to the production
        productions.add(production);

        assert production.getId() != Production.ID_INVALID;

        correspondingProductions.add(production.getId());

    }

    public String createUniqueSymbol(String analogySymbol) {

        if (nonTerminals.containsKey(analogySymbol)) {

            StringBuilder sb = new StringBuilder(analogySymbol);

            do {
                sb.append('\'');
                analogySymbol = sb.toString();
            } while (nonTerminals.containsKey(analogySymbol));

        }

        return analogySymbol;

    }

    private void invalidateCaches() {
        cachedFirstSets = null;
        cachedFollowSets = null;
    }

    public void augment() {

        Augmentor.augment(this);

        invalidateCaches();

    }

    public HashMap<Symbol, HashSet<Symbol>> firstSets() {

        if (cachedFirstSets != null) {
            return cachedFirstSets;
        }

        cachedFirstSets = FirstCalculation.firstSets(this);

        return cachedFirstSets;

    }

    public HashMap<Symbol, HashSet<Symbol>> followSets() {

        if (cachedFollowSets != null) {
            return cachedFollowSets;
        }

        cachedFollowSets = FollowCalculation.followSets(this);

        return cachedFollowSets;

    }

    public HashSet<Symbol> followSet(Symbol nonTerminal) {

        HashMap<Symbol, HashSet<Symbol>> followSets = followSets();

        assert followSets.containsKey(nonTerminal);

        return followSets.get(nonTerminal);

    }

    public HashSet<Symbol> firstSet(Symbol nonTerminal) {

        HashMap<Symbol, HashSet<Symbol>> firstSets = firstSets();

        assert firstSets.containsKey(nonTerminal);

        return firstSets.get(nonTerminal);

    }

    public int getTerminalCount() {
        return terminals.size();
    }

    public int getNonTerminalCount() {
        return nonTerminals.size();
    }

    public Collection<String> getNonTerminals() {
        return nonTerminals.keySet();
    }

    public Collection<String> getTerminals() {
        return terminals.keySet();
    }

    public Collection<Symbol> getNonTerminalSymbols() {
        return nonTerminals.values();
    }

    public Collection<Symbol> getTerminalSymbols() {
        return terminals.values();
    }

    public Production getProduction(int productionId) {
        return productions.get(productionId);
    }

    @Override
    public Iterator<Production> iterator() {
        return productions.iterator();
    }

    public Iterator<Integer> getProductionIdsFor(Symbol nonTerminal) {
        assert productionMap.containsKey(nonTerminal) : "invalid nonTerminal specified";
        return productionMap.get(nonTerminal).iterator();
    }

    public Iterator<Production> getProductionsFor(Symbol nonTerminal) {
        final Iterator<Integer> productionIds = getProductionIdsFor(nonTerminal);
        return new Iterator<Production>() {
            @Override
            public boolean hasNext() {
                return productionIds.hasNext();
            }

            @Override
            public Production next() {
                return getProduction(productionIds.next());
            }
        };
    }

    public Iterator<Symbol> getProductionLabels() {
        return productionMap.keySet().iterator();
    }

    // TODO: get rid of this method
    /**
     * @deprecated
     */
    public Set<Map.Entry<Symbol, ArrayList<Integer>>> getProductions() {
        return productionMap.entrySet();
    }

    public int getProductionCount() {
        return productions.length();
    }

    public Symbol getStartSymbol() {
        assert nonTerminals.containsKey(startSymbol.id) : "start symbol invariant check failed";
        return startSymbol;
    }

    public void setStartSymbol(String newNonTerminal) {
        assert nonTerminals.containsKey(newNonTerminal) : "unknown symbol";
        startSymbol = nonTerminals.get(newNonTerminal);
    }

    public ProductionSymbol getSymbolAfter(Point point) {

        Production production = getProduction(point.productionId);

        assert production != null;
        assert point.position >= 0;
        assert point.position <= production.body.size();

        if (point.position == production.body.size()) {
            return null;
        }

        return production.body.get(point.position);

    }

    public String toString(boolean debug) {

        StringBuilder sb = new StringBuilder();

        Iterator<Map.Entry<Symbol, ArrayList<Integer>>> it = productionMap.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry<Symbol, ArrayList<Integer>> pair = it.next();

            Symbol labelSymbol = pair.getKey();

            String label =
                (labelSymbol == getStartSymbol() ? "S" : " ") +
                labelSymbol.id;

            sb.append(label);
            sb.append(" -> ");

            Iterator<Integer> productionIterator = pair.getValue().iterator();

            if (productionIterator.hasNext()) {

                while (true) {

                    int productionId = productionIterator.next();

                    Production ip = getProduction(productionId);

                    assert ip != null;

                    if (debug) {
                        sb.append("<");
                        sb.append(productionId);
                        sb.append("> ");
                    }

                    sb.append(ip.toString());

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