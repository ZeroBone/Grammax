package net.zerobone.grammax.lexer.tokens;

import net.zerobone.grammax.parser.GrxParser;

public class IdToken extends Token{

    public final String id;

    public IdToken(int line, String id) {
        super(line, GrxParser.T_ID);
        this.id = id;
    }

}