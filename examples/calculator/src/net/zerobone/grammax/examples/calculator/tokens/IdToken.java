package net.zerobone.grammax.examples.calculator.tokens;

import net.zerobone.grammax.examples.calculator.parser.CalcParser;

public class IdToken extends Token {

    public final String id;

    public IdToken(int line, String id) {
        super(line, CalcParser.T_ID);
        this.id = id;
    }

}