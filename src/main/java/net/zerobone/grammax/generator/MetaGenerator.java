package net.zerobone.grammax.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import net.zerobone.grammax.grammar.automation.Automation;
import net.zerobone.grammax.grammar.automation.AutomationProduction;
import net.zerobone.grammax.grammar.automation.AutomationSymbol;

import javax.lang.model.element.Modifier;

class MetaGenerator {

    static void writeConstants(Automation automation, TypeSpec.Builder classBuilder) {

        {
            // T_EOF = 0
            FieldSpec field = FieldSpec.builder(int.class, "T_EOF")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", 0)
                .build();

            classBuilder.addField(field);
        }

        for (int i = 1; i < automation.terminalCount; i++) {

            FieldSpec field = FieldSpec.builder(int.class, "T_" + automation.terminalToSymbol(i))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", i)
                .build();

            classBuilder.addField(field);

        }

        {
            // write terminal count

            FieldSpec field = FieldSpec.builder(int.class, "terminalCount")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", automation.terminalCount)
                .build();

            classBuilder.addField(field);

            field = FieldSpec.builder(int.class, "nonTerminalCount")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", automation.nonTerminalCount)
                .build();

            classBuilder.addField(field);

        }

    }

    static FieldSpec constructGotoTable(Automation automation) {

        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (int s = 0; s < automation.stateCount; s++) {

            sb.append('\n');

            for (int nt = 0; nt < automation.nonTerminalCount; nt++) {

                sb.append(automation.gotoTable[s * automation.nonTerminalCount + nt]);

                if (nt != automation.nonTerminalCount - 1 || s != automation.stateCount - 1) {
                    sb.append(',');
                }

            }

        }

        sb.append('}');

        return FieldSpec.builder(int[].class, "gotoTable")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$L", sb.toString())
            .build();

    }

    static FieldSpec constructActionTable(Automation automation) {

        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (int s = 0; s < automation.stateCount; s++) {

            sb.append('\n');

            for (int t = 0; t < automation.terminalCount; t++) {

                sb.append(automation.actionTable[s * automation.terminalCount + t]);

                if (t != automation.terminalCount - 1 || s != automation.stateCount - 1) {
                    sb.append(',');
                }

            }

        }

        sb.append('}');

        return FieldSpec.builder(int[].class, "actionTable")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$L", sb.toString())
            .build();

    }

    static FieldSpec constructProductionTable(Automation automation) {

        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (AutomationProduction production : automation.productions) {

            sb.append('\n');

            sb.append('{');

            sb.append(production.nonTerminal);

            sb.append(',');

            for (AutomationSymbol symbol : production.body) {

                int id = symbol.isTerminal ? symbol.index : -symbol.index - 1;

                sb.append(id);

                sb.append(',');

            }

            sb.deleteCharAt(sb.length() - 1);

            sb.append('}');
            sb.append(',');

        }

        sb.deleteCharAt(sb.length() - 1);

        sb.append('}');

        return FieldSpec.builder(int[][].class, "productions")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$L", sb.toString())
            .build();

    }

}