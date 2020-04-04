package net.zerobone.grammax.lexer.tokens;

import net.zerobone.grammax.parser.Parser;

public class IdToken extends Token{

    public final String id;

    public IdToken(int line, String id) {
        super(line, Parser.T_ID);
        this.id = id;
    }

}