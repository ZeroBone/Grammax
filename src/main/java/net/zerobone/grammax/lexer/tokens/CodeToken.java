package net.zerobone.grammax.lexer.tokens;

import net.zerobone.grammax.parser.Parser;

public class CodeToken extends Token {

    public final String code;

    public CodeToken(int line, String code) {
        super(line, Parser.T_CODE);
        this.code = code;
    }

}