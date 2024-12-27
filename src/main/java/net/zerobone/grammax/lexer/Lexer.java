package net.zerobone.grammax.lexer;

import net.zerobone.grammax.lexer.tokens.CodeToken;
import net.zerobone.grammax.lexer.tokens.IdToken;
import net.zerobone.grammax.lexer.tokens.Token;
import net.zerobone.grammax.parser.GrxParser;

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
                return constructPrimitiveToken(GrxParser.T_EOF);
            }

            if (current == ' ' || current == '\n' || current == '\t' || current == '\r') {
                continue;
            }
            else if (current == '/') {

                peekChar();

                if (current == '/') {

                    advancePeek();

                    // single-line comment start

                    do {
                        readChar();
                    } while (current != '\n' && current != EOF);

                }

                continue;

            }

            break;

        }

        switch (current) {

            case '=':
                return constructPrimitiveToken(GrxParser.T_ASSIGN);

            case ';':
                return constructPrimitiveToken(GrxParser.T_SEMICOLON);

            case '(':
                return constructPrimitiveToken(GrxParser.T_LEFT_PAREN);

            case ')':
                return constructPrimitiveToken(GrxParser.T_RIGHT_PAREN);

            case '{': {
                // code block

                int nestingLevel = 0;

                StringBuilder sb = new StringBuilder();

                for (;;) {

                    readChar();

                    if (current == EOF) {
                        break;
                    }

                    if (current == '{') {
                        nestingLevel++;
                    }
                    else if (current == '}') {

                        if (nestingLevel == 0) {
                            return new CodeToken(line, sb.toString());
                        }

                        nestingLevel--;

                    }

                    sb.append((char)current);

                }

                throw new LexerException("Reached end of input while scanning code block.", line);

            }

            case '%': {

                readChar();

                if (current == EOF) {
                    throw new LexerException("Unexpected end of file while reading directive header.", line);
                }

                if (!Character.isLetter(current)) {
                    throw new LexerException("Expected identifier after directive begin.", line);
                }

                // return new IdToken(line, readIndentifier());
                String id = readIndentifier();

                if (id.equals("type")) {
                    return constructPrimitiveToken(GrxParser.T_TYPE);
                }
                else if (id.equals("top")) {
                    return constructPrimitiveToken(GrxParser.T_TOP);
                }
                else if (id.equals("name")) {
                    return constructPrimitiveToken(GrxParser.T_NAME);
                }
                else if (id.equals("algo")) {
                    return constructPrimitiveToken(GrxParser.T_ALGO);
                }
                else if (id.equals("target")) {
                    return constructPrimitiveToken(GrxParser.T_TARGET);
                }
                else {
                    throw new LexerException("Unknown directive '" + id + "'", line);
                }

            }

            default:
                break;

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

}