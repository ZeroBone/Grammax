package net.zerobone.grammax.examples.calculator;

import net.zerobone.grammax.examples.calculator.parser.Parser;
import net.zerobone.grammax.examples.calculator.tokens.Token;

import java.io.IOException;
import java.io.InputStream;

public class Calculator {

    private final Lexer lexer;

    private final Parser parser;

    public Calculator(InputStream is) {
        lexer = new Lexer(is);
        parser = new Parser();
    }

    private void repl() {

        System.out.print(">>> ");

        try {

            Token currentToken;

            do {
                currentToken = lexer.lex();
                parser.parse(currentToken.type, currentToken);
            } while (currentToken.type != Parser.T_EOF);

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
            System.err.println("[ERR]: Parse error.");
            return;
        }

        System.out.println("<<< " + (double)parser.getValue());

    }

    public void run() {

        while (true) {
            repl();
            parser.reset();
        }

    }

    public static void main(String[] args) {

        Calculator calculator = new Calculator(System.in);

        calculator.run();

    }

}