package net.zerobone.grammax.generator.lr;

import net.zerobone.grammax.generator.GeneratorContext;
import net.zerobone.grammax.generator.JavaWriter;
import net.zerobone.grammax.generator.MetaGenerator;
import net.zerobone.grammax.grammar.automation.AutomationProduction;
import net.zerobone.grammax.grammar.automation.AutomationSymbol;

import java.io.IOException;

class LRParserClassGenerator {

    private LRParserClassGenerator() {}

    private static void writeStackEntryClass(JavaWriter writer) throws IOException {

        writer.beginIndentedBlock("private static final class StackEntry");

        writer.write("private final int previousState;");
        writer.newLine();

        writer.write("private final Object payload;");
        writer.newLine();

        writer.beginIndentedBlock("private StackEntry(int previousState, Object payload)");

        // constructor body

        writer.addStatement("this.previousState = previousState");
        writer.addStatement("this.payload = payload");

        // end of constructor body

        writer.endIndentedBlock();

        writer.endIndentedBlock();

    }

    private static void writeReductorInterface(JavaWriter writer) throws IOException {

        writer.write("private interface Reductor {");
        writer.newLine();
        writer.enterIndent();

        writer.write("Object reduce(Stack<StackEntry> _grx_stack);");
        writer.newLine();

        writer.exitIndent();
        writer.write('}');
        writer.newLine();

    }

    private static void writeReduceLambdaFor(JavaWriter writer, GeneratorContext context, AutomationProduction production) throws IOException {

        if (production.code == null) {
            for (AutomationSymbol ignored : production.body) {
                writer.addStatement("_grx_stack.pop()");
            }
            writer.addStatement("return null");
            return;
        }

        for (int i = production.body.length - 1; i >= 0; i--) {

            AutomationSymbol symbol = production.body[i];

            if (symbol.argumentName == null) {
                writer.addStatement("_grx_stack.pop()");
                continue;
            }

            String symbolName = symbol.isTerminal ?
                context.config.automation.terminalToSymbol(symbol.index) :
                context.config.automation.nonTerminalToSymbol(symbol.index);

            String symbolType = context.config.getTypeForSymbol(symbolName);

            if (symbolType == null) {
                writer.addStatement("Object " + symbol.argumentName + " = _grx_stack.pop().payload");
            }
            else {
                writer.addStatement(symbolType + " " + symbol.argumentName +
                    " = (" + symbolType + ")_grx_stack.pop().payload");
            }

        }

        writer.addStatement("Object v");

        writer.write('{');
        writer.newLine();
        writer.writeAlign(production.code);
        writer.write('}');

        writer.newLine();
        writer.addStatement("return v");

    }

    private static void writeReductorFor(JavaWriter writer, GeneratorContext context, AutomationProduction production) throws IOException {

        writer.write("new Reductor() {");
        writer.newLine();
        writer.enterIndent();

        writer.write("@Override");
        writer.newLine();

        writer.write("public Object reduce(Stack<StackEntry> _grx_stack) {");
        writer.newLine();
        writer.enterIndent();

        writeReduceLambdaFor(writer, context, production);

        writer.exitIndent();
        writer.write('}');
        writer.newLine();

        writer.exitIndent();
        writer.write('}');

    }

    private static void writeReductionsArray(JavaWriter writer, GeneratorContext context) throws IOException {

        assert context.config.automation.productions.length != 0;

        writer.write("@SuppressWarnings(\"Convert2Lambda\")");
        writer.newLine();
        writer.write("private static final Reductor[] reductions = {");

        writer.newLine();
        writer.enterIndent();

        for (int i = 0;;i++) {

            AutomationProduction production = context.config.automation.productions[i];

            writeReductorFor(writer, context, production);

            if (i == context.config.automation.productions.length - 1) {
                writer.newLine();
                break;
            }

            writer.write(',');
            writer.newLine();

        }

        writer.exitIndent();
        writer.write("};");
        writer.newLine();

    }

    private static void writeParseMethod(JavaWriter writer) throws IOException {

        writer.beginIndentedBlock("public void parse(int tokenId, Object tokenPayload)");

        // method body

        writer.beginIndentedBlock("while (true)");

        writer.addStatement("int action = actionTable[terminalCount * stack.peek().previousState + tokenId]");

        // if action is an error
        writer.beginIndentedBlock("if (action == 0)");
        writer.addStatement("throw new RuntimeException(\"Syntax error\")");
        writer.endIndentedBlock();

        // if action is accept
        writer.beginIndentedBlock("if (action == -1)");
        writer.addStatement("payload = stack.peek().payload");
        writer.addStatement("return");
        writer.endIndentedBlock();

        // if action is shift
        writer.beginIndentedBlock("if (action > 0)");
        writer.addStatement("stack.push(new StackEntry(action - 1, tokenPayload))");
        writer.addStatement("return");
        writer.endIndentedBlock();

        // otherwise the action is reduce

        writer.addStatement("int productionIndex = -action - 2");
        writer.addStatement("Object reducedProduction = reductions[productionIndex].reduce(stack)");
        writer.addStatement("StackEntry newState = stack.peek()");
        writer.addStatement("int nextState = gotoTable[newState.previousState * nonTerminalCount + productionLabels[productionIndex]]");
        writer.addStatement("stack.push(new StackEntry(nextState - 1, reducedProduction))");

        // end of the outer infinite loop
        writer.endIndentedBlock();

        writer.endIndentedBlock();

    }

    private static void writeConstructor(JavaWriter writer, GeneratorContext context) throws IOException {

        writer.write("public ");
        writer.write(context.config.getName());
        writer.write("() ");

        writer.beginIndentedBlock();

        writer.addStatement("stack = new Stack<>()");
        writer.addStatement("stack.push(initialStackEntry)");

        writer.endIndentedBlock();

    }

    private static void writeResetMethod(JavaWriter writer) throws IOException {

        writer.beginIndentedBlock("public void reset()");

        writer.addStatement("stack.clear()");
        writer.addStatement("stack.push(initialStackEntry)");
        writer.addStatement("payload = null");

        writer.endIndentedBlock();

    }

    private static void writeSuccessfullyParsedMethod(JavaWriter writer) throws IOException {

        writer.beginIndentedBlock("public boolean successfullyParsed()");

        writer.addStatement("return payload != null");

        writer.endIndentedBlock();

    }

    private static void writeGetValueMethod(JavaWriter writer) throws IOException {

        writer.beginIndentedBlock("public Object getValue()");

        writer.addStatement("assert payload != null : \"parsing did not succeed\"");
        writer.addStatement("return payload");

        writer.endIndentedBlock();

    }

    static void generate(JavaWriter writer, GeneratorContext context) throws IOException {

        writer.write("public final class ");
        writer.write(context.config.getName());
        writer.write(" {");
        writer.newLine();
        writer.enterIndent();

        // fields

        MetaGenerator.writeConstants(writer, context.config.automation);

        MetaGenerator.writeGotoTable(writer, context.config.automation);

        MetaGenerator.writeActionTable(writer, context.config.automation);

        MetaGenerator.writeProductionLabelTable(writer, context.config.automation);

        writeReductionsArray(writer, context);

        // initialStackEntry
        writer.write("private static final StackEntry initialStackEntry = new StackEntry(0, null);");
        writer.newLine();

        // stack
        writer.write("private Stack<StackEntry> stack;");
        writer.newLine();

        // payload
        writer.write("private Object payload = null;");
        writer.newLine();

        // inner classes

        writeStackEntryClass(writer);

        writeReductorInterface(writer);

        // methods

        writeConstructor(writer, context);

        writeResetMethod(writer);

        writeParseMethod(writer);

        writeSuccessfullyParsedMethod(writer);

        writeGetValueMethod(writer);

        writer.exitIndent();
        writer.write("}");

    }

}