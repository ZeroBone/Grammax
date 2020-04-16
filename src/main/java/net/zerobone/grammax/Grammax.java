package net.zerobone.grammax;

import net.zerobone.grammax.ast.TranslationUnitNode;
import net.zerobone.grammax.ast.statements.StatementNode;
import net.zerobone.grammax.generator.slr.SLRGenerator;
import net.zerobone.grammax.generator.GeneratorContext;
import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.automation.conflict.Conflict;
import net.zerobone.grammax.grammar.automation.Automation;
import net.zerobone.grammax.grammar.verification.GrammarVerification;
import net.zerobone.grammax.grammar.verification.messages.VerificationMessage;
import net.zerobone.grammax.lexer.Lexer;
import net.zerobone.grammax.lexer.LexerException;
import net.zerobone.grammax.lexer.tokens.Token;
import net.zerobone.grammax.parser.GrxParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Grammax {

    public static final String VERSION = "1.0.0-beta";

    private final GrammaxContext context;

    private Grammax(GrammaxContext context) {
        assert context.grammar != null;
        this.context = context;
    }

    private void run() {

        System.out.println("[INF]: Grammax version: " + VERSION);

        GrammarVerification verification = new GrammarVerification(context.grammar);

        verification.verify();

        ArrayList<VerificationMessage> messages = verification.getMessages();

        if (!messages.isEmpty()) {

            boolean errored = false;

            for (VerificationMessage message : messages) {

                if (message.warning) {
                    System.out.print("[WRN]: ");
                    System.out.println(message.getMessage(context.grammar));
                }
                else {
                    errored = true;
                    System.err.print("[ERR]: ");
                    System.err.println(message.getMessage(context.grammar));
                }

            }

            if (errored) {
                return;
            }

        }

        // TODO: check that all the non-terminals are reachable from the start symbol
        // TODO: check that all symbols are defined
        // TODO: warn about right recursion in the grammar

        context.grammar.augment();

        Automation automation = new Automation(context.grammar);

        try {
            exportDebugInfo(automation);
        }
        catch (IOException e) {
            System.err.println("[ERR]: I/O error: " + e.getMessage());
            return;
        }

        if (!automation.getConflicts().isEmpty()) {

            for (Conflict conflict : automation.getConflicts()) {

                System.err.print("[ERR]: ");
                System.err.println(conflict.toString(automation));

            }

            return;

        }

        GeneratorContext generatorContext = new GeneratorContext(
            automation,
            context.getConfiguration()
        );

        try {
            SLRGenerator.generate(generatorContext);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[INF]: Parser generated successfully.");

    }

    private void exportDebugInfo(Automation automation) throws IOException {

        BufferedWriter debugLogWriter = new BufferedWriter(new FileWriter("debug.log"));

        debugLogWriter.write("Grammar:");
        debugLogWriter.newLine();

        debugLogWriter.write(context.grammar.toString(true));

        debugLogWriter.newLine();
        debugLogWriter.newLine();

        debugLogWriter.write("First sets:");
        debugLogWriter.newLine();

        HashMap<Integer, HashSet<Integer>> firstSets = context.grammar.firstSets();

        for (HashMap.Entry<Integer, HashSet<Integer>> entry : firstSets.entrySet()) {

            debugLogWriter.write("FIRST(");
            debugLogWriter.write(context.grammar.idToSymbol(entry.getKey()));
            debugLogWriter.write(") = {");

            Iterator<Integer> firstSetIterator = entry.getValue().iterator();

            if (firstSetIterator.hasNext()) {

                while (true) {

                    int id = firstSetIterator.next();

                    if (id != Grammar.FIRST_FOLLOW_SET_EPSILON) {

                        debugLogWriter.write(context.grammar.idToSymbol(id));

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

        HashMap<Integer, HashSet<Integer>> followSets = context.grammar.followSets();

        for (HashMap.Entry<Integer, HashSet<Integer>> entry : followSets.entrySet()) {

            debugLogWriter.write("FOLLOW(");
            debugLogWriter.write(context.grammar.idToSymbol(entry.getKey()));
            debugLogWriter.write(") = {");

            Iterator<Integer> followSetIterator = entry.getValue().iterator();

            if (followSetIterator.hasNext()) {

                while (true) {

                    int id = followSetIterator.next();

                    if (id == Grammar.TERMINAL_EOF) {
                        debugLogWriter.write("$");
                    }
                    else if (id != Grammar.FIRST_FOLLOW_SET_EPSILON) {

                        debugLogWriter.write(context.grammar.idToSymbol(id));

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

    private static void run(TranslationUnitNode translationUnit) {

        GrammaxContext context = new GrammaxContext();

        for (StatementNode statement : translationUnit.statements) {
            statement.apply(context);
        }

        if (context.hasErrors()) {
            context.printErrors();
            return;
        }

        new Grammax(context).run();

    }

    public static void main(String[] args) {

        if (args.length == 1) {

            InputStream is;

            try {
                is = new FileInputStream(args[0]);
            }
            catch (FileNotFoundException e) {
                System.err.println("[ERR]: I/O error: File '" + args[0] + "' was not found!");
                return;
            }

            Lexer lexer = new Lexer(is);
            GrxParser parser = new GrxParser();

            try {

                Token currentToken;

                do {
                    currentToken = lexer.lex();
                    parser.parse(currentToken.type, currentToken);
                } while (currentToken.type != GrxParser.T_EOF);

            }
            catch (LexerException e) {
                System.err.println("[ERR]: Syntax error: " + e.getMessage());
                return;
            }
            catch (IOException e) {
                System.err.println("[ERR]: I/O error: " + e.getMessage());
                return;
            }

            if (!parser.successfullyParsed()) {
                System.err.println("Error while parsing the grammar file.");
                // TODO: display parsing errors
                return;
            }

            TranslationUnitNode translationUnit = (TranslationUnitNode)parser.getValue();

            assert translationUnit != null;

            run(translationUnit);

            return;

        }

        System.err.println("[ERR]: Invalid arguments!");
        System.out.println("[INF]: Usage: grammax filename.grx");

    }

}