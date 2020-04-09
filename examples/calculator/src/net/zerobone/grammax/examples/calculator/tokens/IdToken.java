package net.zerobone.grammax.examples.calculator.tokens;

import net.zerobone.grammax.examples.calculator.parser.Parser;

public class IdToken extends Token {

    public final String id;

    public IdToken(int line, String id) {
        super(line, Parser.T_ID);
        this.id = id;
    }

}