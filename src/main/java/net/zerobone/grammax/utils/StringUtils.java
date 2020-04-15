package net.zerobone.grammax.utils;

public class StringUtils {

    private StringUtils() {}

    public static boolean startsWithCapital(String s) {
        return Character.isUpperCase(s.charAt(0));
    }

    public static boolean isTerminal(String s) {
        return startsWithCapital(s);
    }

    public static boolean isNonTerminal(String s) {
        return !isTerminal(s);
    }

}