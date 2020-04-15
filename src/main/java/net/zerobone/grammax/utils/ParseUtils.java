package net.zerobone.grammax.utils;

import net.zerobone.grammax.parser.GrxParser;

public class ParseUtils {

    public static String convertTerminal(int terminal) {

        switch (terminal) {

            case GrxParser.T_EOF:
                return "<end-of-source>";

            case GrxParser.T_CODE:
                return "<code-block>";

            case GrxParser.T_SEMICOLON:
                return ";";

            case GrxParser.T_RIGHT_PAREN:
                return ")";

            case GrxParser.T_ID:
                return "<identifier>";

            case GrxParser.T_ASSIGN:
                return "=";

            case GrxParser.T_LEFT_PAREN:
                return "(";

            case GrxParser.T_TYPE:
                return "%type";

            default:
                return "<" + terminal + ">";

        }

    }

}