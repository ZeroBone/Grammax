package net.zerobone.grammax;

public enum TargetLanguage {
    JAVA,
    CPP;

    public static final TargetLanguage DEFAULT = JAVA;

    public static TargetLanguage fromString(String languageName) {

        if (languageName.equals("java")) {
            return JAVA;
        }

        if (languageName.equals("cpp")) {
            return CPP;
        }

        return null;

    }
}
