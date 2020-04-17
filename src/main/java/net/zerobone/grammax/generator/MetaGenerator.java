package net.zerobone.grammax.generator;

import net.zerobone.grammax.grammar.automation.Automation;
import net.zerobone.grammax.grammar.automation.AutomationProduction;

import java.io.IOException;

public class MetaGenerator {

    public static void writeConstants(JavaWriter writer, Automation automation) throws IOException {

        for (int i = 0; i < automation.getTerminalCount(); i++) {

            writer.write("public static final int T_");
            writer.write(automation.terminalToSymbol(i));
            writer.write(" = ");
            writer.write(String.valueOf(i));
            writer.write(";");
            writer.newLine();

        }

        // write terminal count

        writer.write("private static final int terminalCount = ");
        writer.write(String.valueOf(automation.getTerminalCount()));
        writer.write(";");
        writer.newLine();

        // write non-terminal count

        writer.write("private static final int nonTerminalCount = ");
        writer.write(String.valueOf(automation.getNonTerminalCount()));
        writer.write(";");
        writer.newLine();

    }

    public static void writeGotoTable(JavaWriter writer, Automation automation) throws IOException {

        writer.write("private static final int[] gotoTable = {");

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

    public static void writeActionTable(JavaWriter writer, Automation automation) throws IOException {

        writer.write("private static final int[] actionTable = {");

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

    public static void writeProductionLabelTable(JavaWriter writer, Automation automation) throws IOException {

        writer.write("private static final int[] productionLabels = {");

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