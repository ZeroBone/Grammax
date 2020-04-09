package net.zerobone.grammax.examples.calculator;

public class LexerException extends Exception {

    public final int line;

    public LexerException(String message, int line) {
        super(message);
        this.line = line;
    }

}