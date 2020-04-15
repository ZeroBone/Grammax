package net.zerobone.grammax.generator;

import net.zerobone.grammax.generator.slr.SLRGenerator;
import net.zerobone.grammax.grammar.automation.Automation;
import net.zerobone.grammax.grammar.automation.AutomationProduction;

import java.io.BufferedWriter;
import java.io.IOException;

public class MetaGenerator {

    public static void writeConstants(JavaWriter writer, Automation automation) throws IOException {

        writer.write("public static final int T_EOF = 0;");
        writer.newLine();

        for (int i = 1; i < automation.terminalCount; i++) {

            writer.write("public static final int T_");
            writer.write(automation.terminalToSymbol(i));
            writer.write(" = ");
            writer.write(String.valueOf(i));
            writer.write(";");
            writer.newLine();

        }

        // write terminal count

        writer.write("private static final int terminalCount = ");
        writer.write(String.valueOf(automation.terminalCount));
        writer.write(";");
        writer.newLine();

        // write non-terminal count

        writer.write("private static final int nonTerminalCount = ");
        writer.write(String.valueOf(automation.nonTerminalCount));
        writer.write(";");
        writer.newLine();

    }

    public static void writeGotoTable(JavaWriter writer, Automation automation) throws IOException {

        writer.write("private static final int[] gotoTable = {");

        writer.enterIndent();

        for (int s = 0; s < automation.stateCount; s++) {

            writer.newLine();

            for (int nt = 0; nt < automation.nonTerminalCount; nt++) {

                writer.write(String.valueOf(automation.gotoTable[s * automation.nonTerminalCount + nt]));

                if (nt != automation.nonTerminalCount - 1 || s != automation.stateCount - 1) {
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

            for (int t = 0; t < automation.terminalCount; t++) {

                writer.write(String.valueOf(automation.actionTable[s * automation.terminalCount + t]));

                if (t != automation.terminalCount - 1 || s != automation.stateCount - 1) {
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