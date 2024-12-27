package net.zerobone.grammax;

import net.zerobone.grammax.ast.TranslationUnitNode;
import net.zerobone.grammax.ast.statements.StatementNode;
import net.zerobone.grammax.generator.lr.LRGenerator;
import net.zerobone.grammax.generator.GeneratorContext;
import net.zerobone.grammax.grammar.Symbol;
import net.zerobone.grammax.grammar.automation.conflict.Conflict;
import net.zerobone.grammax.grammar.automation.Automation;
import net.zerobone.grammax.grammar.verification.GrammarVerification;
import net.zerobone.grammax.grammar.verification.messages.VerificationMessage;
import net.zerobone.grammax.lexer.Lexer;
import net.zerobone.grammax.lexer.LexerException;
import net.zerobone.grammax.lexer.tokens.Token;
import net.zerobone.grammax.parser.GrxParser;
import net.zerobone.grammax.utils.ParseUtils;

import java.io.*;
import java.util.*;

public class Grammax {

    public static final String VERSION = "1.1.1";

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

        context.grammar.augment();

        GrammaxConfiguration configuration = context.getConfiguration();

        try {
            exportDebugInfo(configuration.automation, configuration.getName());
        }
        catch (IOException e) {
            System.err.println("[ERR]: I/O error: " + e.getMessage());
            return;
        }

        if (!configuration.automation.getConflicts().isEmpty()) {

            for (Conflict conflict : configuration.automation.getConflicts()) {

                System.err.print("[ERR]: ");
                System.err.println(conflict.toString(configuration.automation));

            }

            return;

        }

        GeneratorContext generatorContext = new GeneratorContext(configuration);

        try {
            LRGenerator.generate(generatorContext);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[INF]: Parser generated successfully.");

    }

    private void exportDebugInfo(Automation automation, String parserName) throws IOException {

        BufferedWriter debugLogWriter = new BufferedWriter(
            new FileWriter(parserName + "_debug.txt")
        );

        debugLogWriter.write("Grammar:");
        debugLogWriter.newLine();

        debugLogWriter.write(context.grammar.toString(true));

        debugLogWriter.newLine();
        debugLogWriter.newLine();

        debugLogWriter.write("First sets:");
        debugLogWriter.newLine();

        HashMap<Symbol, HashSet<Symbol>> firstSets = context.grammar.firstSets();

        for (Map.Entry<Symbol, HashSet<Symbol>> entry : firstSets.entrySet()) {

            debugLogWriter.write("FIRST(");
            debugLogWriter.write(entry.getKey().id);
            debugLogWriter.write(") = ");

            debugLogWriter.write(Symbol.prettyPrintSet(entry.getValue()));

            debugLogWriter.newLine();

        }

        debugLogWriter.newLine();

        debugLogWriter.write("Follow sets:");
        debugLogWriter.newLine();

        HashMap<Symbol, HashSet<Symbol>> followSets = context.grammar.followSets();

        for (Map.Entry<Symbol, HashSet<Symbol>> entry : followSets.entrySet()) {

            debugLogWriter.write("FOLLOW(");
            debugLogWriter.write(entry.getKey().id);
            debugLogWriter.write(") = ");

            debugLogWriter.write(Symbol.prettyPrintSet(entry.getValue()));

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

            {
                Token currentToken = null;

                try {

                    do {
                        currentToken = lexer.lex();
                        parser.parse(currentToken.type, currentToken);
                    } while (currentToken.type != GrxParser.T_EOF);

                }
                catch (RuntimeException e) {
                    if (currentToken == null) {
                        System.err.println("[ERR]: Fatal syntax error!");
                    }
                    else {
                        System.err.println("[ERR]: Syntax error at line " + currentToken.line
                            + ", before " + ParseUtils.convertTerminal(currentToken.type) + ".");
                    }
                    return;
                }
                catch (LexerException e) {
                    System.err.println("[ERR]: Syntax error: " + e.getMessage());
                    return;
                }
                catch (IOException e) {
                    System.err.println("[ERR]: I/O error: " + e.getMessage());
                    return;
                }
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