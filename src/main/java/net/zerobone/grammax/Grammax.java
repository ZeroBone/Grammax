package net.zerobone.grammax;

import net.zerobone.grammax.ast.TranslationUnitNode;
import net.zerobone.grammax.ast.entities.ProductionSymbol;
import net.zerobone.grammax.ast.statements.ProductionStatementNode;
import net.zerobone.grammax.ast.statements.StatementNode;
import net.zerobone.grammax.ast.statements.TypeStatementNode;
import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Production;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.lr.LRItems;
import net.zerobone.grammax.grammar.slr.conflict.Conflict;
import net.zerobone.grammax.grammar.slr.Automation;
import net.zerobone.grammax.lexer.Lexer;
import net.zerobone.grammax.lexer.LexerException;
import net.zerobone.grammax.lexer.tokens.Token;
import net.zerobone.grammax.parser.ParseError;
import net.zerobone.grammax.parser.ParseUtils;
import net.zerobone.grammax.parser.Parser;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Grammax {

    private static final String VERSION = "1.0.0-beta";

    private Grammar grammar;

    private final HashMap<String, String> typeMap; // TODO: use type map

    private Grammax(Grammar grammar, HashMap<String, String> typeMap) {
        this.grammar = grammar;
        this.typeMap = typeMap;
    }

    private void run() {

        System.out.println("Grammax version: " + VERSION);

        // TODO: check that all the non-terminals are reachable from the start symbol

        grammar.augment();

        LRItems items = new LRItems(grammar);

        Automation automation = new Automation(grammar, items);

        try {
            exportDebugInfo(automation);
        }
        catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            return;
        }

        if (!automation.getConflicts().isEmpty()) {

            for (Conflict conflict : automation.getConflicts()) {

                System.err.println("[CONFLICT]: " + conflict.toString(automation));

            }

            return;

        }

        System.out.println("Success");

    }

    private void exportDebugInfo(Automation automation) throws IOException {

        BufferedWriter debugLogWriter = new BufferedWriter(new FileWriter("debug.log"));

        debugLogWriter.write("Grammar:");
        debugLogWriter.newLine();

        debugLogWriter.write(grammar.toString(true));

        debugLogWriter.newLine();
        debugLogWriter.newLine();

        debugLogWriter.write("First sets:");
        debugLogWriter.newLine();

        HashMap<Integer, HashSet<Integer>> firstSets = grammar.firstSets();

        for (HashMap.Entry<Integer, HashSet<Integer>> entry : firstSets.entrySet()) {

            debugLogWriter.write("FIRST(");
            debugLogWriter.write(grammar.idToSymbol(entry.getKey()));
            debugLogWriter.write(") = {");

            Iterator<Integer> firstSetIterator = entry.getValue().iterator();

            if (firstSetIterator.hasNext()) {

                while (true) {

                    int id = firstSetIterator.next();

                    if (id != Grammar.FIRST_FOLLOW_SET_EPSILON) {

                        debugLogWriter.write(grammar.idToSymbol(id));

                    }

                    if (!firstSetIterator.hasNext()) {
                        break;
                    }

                    debugLogWriter.write(", ");

                }

            }

            debugLogWriter.write('}');

            debugLogWriter.newLine();

        }

        debugLogWriter.newLine();

        debugLogWriter.write("Follow sets:");
        debugLogWriter.newLine();

        HashMap<Integer, HashSet<Integer>> followSets = grammar.followSets();

        for (HashMap.Entry<Integer, HashSet<Integer>> entry : followSets.entrySet()) {

            debugLogWriter.write("FOLLOW(");
            debugLogWriter.write(grammar.idToSymbol(entry.getKey()));
            debugLogWriter.write(") = {");

            Iterator<Integer> followSetIterator = entry.getValue().iterator();

            if (followSetIterator.hasNext()) {

                while (true) {

                    int id = followSetIterator.next();

                    if (id == Grammar.TERMINAL_EOF) {
                        debugLogWriter.write("$");
                    }
                    else if (id != Grammar.FIRST_FOLLOW_SET_EPSILON) {

                        debugLogWriter.write(grammar.idToSymbol(id));

                    }

                    if (!followSetIterator.hasNext()) {
                        break;
                    }

                    debugLogWriter.write(", ");

                }

            }

            debugLogWriter.write('}');

            debugLogWriter.newLine();

        }

        debugLogWriter.newLine();
        debugLogWriter.write("Automation:");
        debugLogWriter.newLine();

        debugLogWriter.write(automation.toString());

        debugLogWriter.close();

    }

    private static Production convertProduction(ProductionStatementNode statement) {

        Production production = new Production(statement.code);

        for (ProductionSymbol symbol : statement.production) {
            production.append(new Symbol(symbol.id, symbol.terminal, symbol.argument));
        }

        return production;

    }

    private static void run(TranslationUnitNode translationUnit) {

        Grammar grammar = null;

        HashMap<String, String> typeMap = new HashMap<>();

        for (StatementNode stmt : translationUnit.statements) {

            if (stmt instanceof ProductionStatementNode) {

                ProductionStatementNode production = (ProductionStatementNode)stmt;

                if (grammar == null) {
                    grammar = new Grammar(production.nonTerminal, convertProduction(production));
                }
                else {
                    grammar.addProduction(production.nonTerminal, convertProduction(production));
                }

            }
            else if (stmt instanceof TypeStatementNode) {

                String symbol = ((TypeStatementNode)stmt).symbol;

                String type = ((TypeStatementNode)stmt).type;

                if (typeMap.containsKey(symbol)) {
                    System.err.println("Error: Duplicate type declaration for symbol '" + symbol + "'.");
                    return;
                }

                typeMap.put(symbol, type);

            }

        }

        if (grammar == null) {
            System.err.println("Error: could not find start symbol in grammar.");
            return;
        }

        new Grammax(grammar, typeMap).run();

    }

    public static void main(String[] args) {

        if (args.length == 1) {

            InputStream is;

            try {
                is = new FileInputStream(args[0]);
            }
            catch (FileNotFoundException e) {
                System.out.println("I/O error: File '" + args[0] + "' was not found!");
                return;
            }

            Lexer lexer = new Lexer(is);
            Parser parser = new Parser();

            try {

                Token currentToken;

                do {
                    currentToken = lexer.lex();
                    parser.parse(currentToken.type, currentToken);
                } while (currentToken.type != Parser.T_EOF);

            }
            catch (LexerException e) {
                System.err.println("Syntax error: " + e.getMessage());
                return;
            }
            catch (IOException e) {
                System.err.println("I/O error: " + e.getMessage());
                return;
            }

            if (!parser.successfullyParsed()) {

                for (ParseError error : parser.getErrors()) {

                    if (error.expected != ParseError.ANY) {
                        System.err.println(
                            "Syntax Error: Expected '" + ParseUtils.convertTerminal(error.expected) +
                                "', got '" + ParseUtils.convertTerminal(error.got) +
                                "' at line " + ((Token)error.token).line
                        );
                    }
                    else {
                        System.err.println(
                            "Syntax Error: Unexpected '" + ParseUtils.convertTerminal(error.got) +
                                "' at line " + ((Token)error.token).line
                        );
                    }

                }

                return;
            }

            TranslationUnitNode translationUnit = (TranslationUnitNode)parser.getValue();

            assert translationUnit != null;

            run(translationUnit);

            return;

        }

        System.err.println("Invalid arguments!");
        System.out.println("Usage: grammax filename.grx");

    }

}