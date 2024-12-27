package net.zerobone.grammax.generator;

import net.zerobone.grammax.TargetLanguage;
import net.zerobone.grammax.grammar.automation.Automation;
import net.zerobone.grammax.grammar.automation.AutomationProduction;

import java.io.IOException;

public class MetaGenerator {

    public static void writeConstants(ProgramWriter writer, Automation automation, TargetLanguage targetLanguage) throws IOException {

        for (int i = 0; i < automation.getTerminalCount(); i++) {

            switch (targetLanguage) {
                case JAVA:
                    writer.write("public static final int T_");
                    writer.write(automation.terminalToSymbol(i));
                    writer.write(" = ");
                    writer.write(String.valueOf(i));
                    writer.write(";");
                    break;
                case CPP:
                    writer.cancelIndentationForPresentLine();
                    writer.write("#define T_");
                    writer.write(automation.terminalToSymbol(i));
                    writer.write(' ');
                    writer.write(String.valueOf(i));
                    break;
                default:
                    assert false;
                    break;
            }

            writer.newLine();

        }

        // write terminal count

        switch (targetLanguage) {
            case JAVA:
                writer.write("private static final int terminalCount = ");
                writer.write(String.valueOf(automation.getTerminalCount()));
                writer.write(";");
                break;
            case CPP:
                writer.cancelIndentationForPresentLine();
                writer.write("#define TERMINAL_COUNT ");
                writer.write(String.valueOf(automation.getTerminalCount()));
                break;
            default:
                assert false;
                break;
        }

        writer.newLine();

        // write non-terminal count

        switch (targetLanguage) {
            case JAVA:
                writer.write("private static final int nonTerminalCount = ");
                writer.write(String.valueOf(automation.getNonTerminalCount()));
                writer.write(";");
                break;
            case CPP:
                writer.cancelIndentationForPresentLine();
                writer.write("#define NON_TERMINAL_COUNT ");
                writer.write(String.valueOf(automation.getNonTerminalCount()));
                break;
            default:
                assert false;
                break;
        }

        writer.newLine();

    }

    public static void writeGotoTable(ProgramWriter writer, Automation automation, TargetLanguage targetLanguage) throws IOException {

        switch (targetLanguage) {
            case JAVA:
                writer.write("private static final int[] gotoTable = {");
                break;
            case CPP:
                writer.write("static constexpr int gotoTable[] = {");
                break;
            default:
                assert false;
                break;
        }

        writer.enterIndent();

        for (int s = 0; s < automation.stateCount; s++) {

            writer.newLine();

            for (int nt = 0; nt < automation.getNonTerminalCount(); nt++) {

                writer.write(String.valueOf(automation.gotoTable[s * automation.getNonTerminalCount() + nt]));

                if (nt != automation.getNonTerminalCount() - 1 || s != automation.stateCount - 1) {
                    writer.write(',');
                }

            }

        }

        writer.exitIndent();

        writer.write("};");
        writer.newLine();

    }

    public static void writeActionTable(ProgramWriter writer, Automation automation, TargetLanguage targetLanguage) throws IOException {

        switch (targetLanguage) {
            case JAVA:
                writer.write("private static final int[] actionTable = {");
                break;
            case CPP:
                writer.write("static constexpr int actionTable[] = {");
                break;
            default:
                assert false;
                break;
        }

        writer.enterIndent();

        for (int s = 0; s < automation.stateCount; s++) {

            writer.newLine();

            for (int t = 0; t < automation.getTerminalCount(); t++) {

                writer.write(String.valueOf(automation.actionTable[s * automation.getTerminalCount() + t]));

                if (t != automation.getTerminalCount() - 1 || s != automation.stateCount - 1) {
                    writer.write(',');
                }

            }

        }

        writer.exitIndent();

        writer.write("};");

        writer.newLine();

    }

    public static void writeProductionLabelTable(ProgramWriter writer, Automation automation, TargetLanguage targetLanguage) throws IOException {

        switch (targetLanguage) {
            case JAVA:
                writer.write("private static final int[] productionLabels = {");
                break;
            case CPP:
                writer.write("static constexpr int productionLabels[] = {");
                break;
            default:
                assert false;
                break;
        }


        assert automation.productions.length != 0;

        for (int i = 0;;i++) {

            AutomationProduction production = automation.productions[i];

            writer.write(String.valueOf(production.nonTerminal));

            if (i == automation.productions.length - 1) {
                break;
            }

            writer.write(',');

        }

        writer.write("};");
        writer.newLine();

    }

}