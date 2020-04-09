package net.zerobone.grammax.examples.calculator;

import net.zerobone.grammax.examples.calculator.parser.Parser;
import net.zerobone.grammax.examples.calculator.tokens.IdToken;
import net.zerobone.grammax.examples.calculator.tokens.NumberToken;
import net.zerobone.grammax.examples.calculator.tokens.Token;

import java.io.IOException;
import java.io.InputStream;

public class Lexer {

    private static final int EOF = -1;

    private final InputStream stream;

    private int line = 1;

    private int current;

    private boolean peeking = false;

    public Lexer(InputStream stream) {
        this.stream = stream;
    }

    public void reset() {
        line = 1;
        peeking = false;
    }

    private void readChar() throws IOException {

        if (peeking) {
            peeking = false;
            return;
        }

        current = stream.read();

        if (current == '\n') {
            line++;
        }

    }

    private void peekChar() throws IOException {

        // assert !peeking;

        peeking = true;

        current = stream.read();

        if (current == '\n') {
            line++;
        }

    }

    private void advancePeek() {
        assert peeking;
        peeking = false;
    }

    private Token constructPrimitiveToken(int type) {
        return new Token(line, type);
    }

    public Token lex() throws IOException, LexerException {

        for (;;) {

            readChar();

            if (current == -1) {
                return constructPrimitiveToken(Parser.T_EOF);
            }

            if (current == ' ' || current == '\t' || current == '\r') {
                continue;
            }

            break;

        }

        switch (current) {

            case '+':
                return constructPrimitiveToken(Parser.T_PLUS);

            case '*':
                return constructPrimitiveToken(Parser.T_MUL);

            case '(':
                return constructPrimitiveToken(Parser.T_LPAREN);

            case ')':
                return constructPrimitiveToken(Parser.T_RPAREN);

            case '\n':
                return constructPrimitiveToken(Parser.T_EOF);

            default:
                break;

        }

        if (Character.isDigit(current)) {
            return new NumberToken(line, readNumber());
        }

        if (Character.isLetter(current)) {
            return new IdToken(line, readIndentifier());
        }

        throw new LexerException("Invalid start of lexeme '" + (char)current + "'", line);

    }

    private String readIndentifier() throws IOException {

        StringBuilder sb = new StringBuilder();

        do {

            sb.append((char)current);

            peekChar();

            if (current == EOF) {
                break;
            }

        } while (Character.isLetterOrDigit((char)current) || current == '_');

        return sb.toString();

    }

    private double readNumber() throws IOException {

        double n = 0;

        while (true) {

            n = n * 10 + ((char)current - '0');

            peekChar();

            if (current == EOF) {
                break;
            }

            if (Character.isDigit((char)current)) {
                continue;
            }

            if ((char)current == '.') {
                return readFloatingPoint(n);
            }

            break;

        }

        return n;

    }

    private double readFloatingPoint(double fixedPoint) throws IOException {

        double floatingPoint = 1;

        while (true) {

            peekChar();

            if (current == EOF) {
                break;
            }

            if (!Character.isDigit((char)current)) {
                break;
            }

            fixedPoint = fixedPoint * 10 + ((char)current - '0');

            floatingPoint *= 10;

        }

        return fixedPoint / floatingPoint;

    }

}