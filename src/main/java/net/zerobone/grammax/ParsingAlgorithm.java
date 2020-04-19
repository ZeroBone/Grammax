package net.zerobone.grammax;

public enum ParsingAlgorithm {
    SLR,
    CLR,
    LALR;

    public static final ParsingAlgorithm DEFAULT = LALR;

    public static ParsingAlgorithm fromString(String algorithmName) {

        if (algorithmName.equals("SLR")) {
            return SLR;
        }

        if (algorithmName.equals("CLR")) {
            return CLR;
        }

        if (algorithmName.equals("LALR")) {
            return LALR;
        }

        return null;

    }

}