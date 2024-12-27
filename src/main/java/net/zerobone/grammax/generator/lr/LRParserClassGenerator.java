package net.zerobone.grammax.generator.lr;

import net.zerobone.grammax.TargetLanguage;
import net.zerobone.grammax.generator.GeneratorContext;
import net.zerobone.grammax.generator.ProgramWriter;
import net.zerobone.grammax.generator.MetaGenerator;
import net.zerobone.grammax.grammar.automation.AutomationProduction;
import net.zerobone.grammax.grammar.automation.AutomationSymbol;

import java.io.IOException;
import java.util.Map;

class LRParserClassGenerator {

    private LRParserClassGenerator() {}

    private static void writeStackEntryJavaClass(ProgramWriter writer) throws IOException {

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

    private static void writeStackEntryPayloadCPPUnion(ProgramWriter writer, GeneratorContext context) throws IOException {

        writer.cancelIndentationForPresentLine();
        writer.write("public:");
        writer.newLine();

        writer.beginIndentedBlock("union StackEntryPayload");

        writer.write("void* _grx_object;");
        writer.newLine();

        for (Map.Entry<String, String> entry : context.config.getSymbolToTypeMap().entrySet()) {
            String symbol = entry.getKey();
            String symbolType = entry.getValue();
            writer.write(symbolType);
            writer.write(' ');
            writer.write(symbol);
            writer.write(';');
            writer.newLine();
        }

        writer.newLine();

        // constructor
        writer.write("StackEntryPayload() {}");
        writer.newLine();

        // destructor
        writer.write("~StackEntryPayload() {}");
        writer.newLine();

        // move constructor
        writer.beginIndentedBlock("StackEntryPayload(StackEntryPayload&& other) noexcept");
        writer.addStatement("std::memcpy(this, &other, sizeof(StackEntryPayload))");
        writer.endIndentedBlock();

        // copy constructor
        writer.beginIndentedBlock("StackEntryPayload(const StackEntryPayload& other) noexcept");
        writer.addStatement("std::memcpy(this, &other, sizeof(StackEntryPayload))");
        writer.endIndentedBlock();

        // move assignment operator
        writer.beginIndentedBlock("StackEntryPayload& operator=(StackEntryPayload&& other) noexcept");
        writer.beginIndentedBlock("if (this != &other)");
        writer.addStatement("std::memcpy(this, &other, sizeof(StackEntryPayload))");
        writer.endIndentedBlock();
        writer.addStatement("return *this");
        writer.endIndentedBlock();

        // copy assignment operator
        writer.beginIndentedBlock("StackEntryPayload& operator=(const StackEntryPayload& other)");
        writer.beginIndentedBlock("if (this != &other)");
        writer.addStatement("std::memcpy(this, &other, sizeof(StackEntryPayload))");
        writer.endIndentedBlock();
        writer.addStatement("return *this");
        writer.endIndentedBlock();

        writer.endIndentedBlock(true);

        writer.cancelIndentationForPresentLine();
        writer.write("private:");
        writer.newLine();

    }

    private static void writeStackEntryCPPStruct(ProgramWriter writer) throws IOException {

        writer.beginIndentedBlock("struct StackEntry");

        writer.write("int previousState;");
        writer.newLine();

        writer.write("StackEntryPayload payload;");
        writer.newLine();

        writer.write("StackEntry(int previousState, StackEntryPayload payload) : previousState(previousState), payload(std::move(payload)) {}");
        writer.newLine();

        writer.beginIndentedBlock("StackEntry() : previousState(0)");
        writer.addStatement("payload._grx_object = nullptr");
        writer.endIndentedBlock();
        // writer.newLine();

        // move constructor
        writer.write("StackEntry(StackEntry&&) = default;");
        writer.newLine();

        writer.endIndentedBlock(true);

    }

    private static void writeReductorJavaInterface(ProgramWriter writer) throws IOException {

        writer.write("private interface Reductor {");
        writer.newLine();
        writer.enterIndent();

        writer.write("Object reduce(Stack<StackEntry> _grx_stack);");
        writer.newLine();

        writer.exitIndent();
        writer.write('}');
        writer.newLine();

    }

    private static void writeReduceLambdaFor(ProgramWriter writer, GeneratorContext context, AutomationProduction production) throws IOException {

        if (production.code == null) {

            for (AutomationSymbol ignored : production.body) {
                switch (context.config.targetLanguage) {
                    case JAVA:
                        writer.addStatement("_grx_stack.pop()");
                        break;
                    case CPP:
                        writer.addStatement("_grx_stack->pop_back()");
                        break;
                    default:
                        assert false;
                        break;
                }
            }

            switch (context.config.targetLanguage) {
                case JAVA:
                    writer.addStatement("return null");
                    break;
                case CPP:
                    writer.addStatement("return {._grx_object = nullptr}");
                    break;
                default:
                    assert false;
                    break;
            }

            return;

        }

        for (int i = production.body.length - 1; i >= 0; i--) {

            AutomationSymbol symbol = production.body[i];

            if (symbol.argumentName == null) {
                switch (context.config.targetLanguage) {
                    case JAVA:
                        writer.addStatement("_grx_stack.pop()");
                        break;
                    case CPP:
                        writer.addStatement("_grx_stack->pop_back()");
                        break;
                    default:
                        assert false;
                        break;
                }
                continue;
            }

            String symbolName = symbol.isTerminal ?
                context.config.automation.terminalToSymbol(symbol.index) :
                context.config.automation.nonTerminalToSymbol(symbol.index);

            String symbolType = context.config.getTypeForSymbol(symbolName);

            switch (context.config.targetLanguage) {
                case JAVA:
                    if (symbolType == null) {
                        writer.addStatement("Object " + symbol.argumentName + " = _grx_stack.pop().payload");
                    }
                    else {
                        writer.addStatement(symbolType + " " + symbol.argumentName +
                            " = (" + symbolType + ")_grx_stack.pop().payload");
                    }
                    break;
                case CPP:

                    if (symbolType == null) {
                        writer.addStatement("void* " + symbol.argumentName + " = _grx_stack->back().payload._grx_object");
                    }
                    else {
                        writer.addStatement(symbolType + " " + symbol.argumentName + " = _grx_stack->back().payload." + symbolName);
                    }

                    writer.addStatement("_grx_stack->pop_back()");

                    break;
                default:
                    assert false;
                    break;
            }

        }

        String productionNonTerminalSymbol = context.config.automation.nonTerminalToSymbol(production.nonTerminal);

        switch (context.config.targetLanguage) {
            case JAVA:
                writer.addStatement("Object v");
                break;
            case CPP:
                String productionNonTerminalSymbolType = context.config.getTypeForSymbol(productionNonTerminalSymbol);

                if (productionNonTerminalSymbolType == null) {
                    productionNonTerminalSymbolType = "void*";
                }

                writer.addStatement(productionNonTerminalSymbolType + " v");

                break;
            default:
                assert false;
                break;
        }

        writer.write('{');
        writer.newLine();
        writer.writeAlign(production.code);
        writer.write('}');

        writer.newLine();

        switch (context.config.targetLanguage) {
            case JAVA:
                writer.addStatement("return v");
                break;
            case CPP:
                writer.addStatement("StackEntryPayload _grx_v");
                writer.addStatement("_grx_v." + productionNonTerminalSymbol + " = v");
                writer.addStatement("return _grx_v");
                break;
            default:
                assert false;
                break;
        }

    }

    private static void writeJavaReductorFor(ProgramWriter writer, GeneratorContext context, AutomationProduction production) throws IOException {

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

    private static void writeReductionsArray(ProgramWriter writer, GeneratorContext context) throws IOException {

        assert context.config.automation.productions.length != 0;

        switch (context.config.targetLanguage) {
            case JAVA:
                writer.write("@SuppressWarnings(\"Convert2Lambda\")");
                writer.newLine();
                writer.write("private static final Reductor[] reductions = {");

                writer.newLine();
                writer.enterIndent();

                for (int i = 0;;i++) {

                    AutomationProduction production = context.config.automation.productions[i];

                    writeJavaReductorFor(writer, context, production);

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

                break;
            case CPP:
                // generate function definition for every production

                for (int i = 0; i < context.config.automation.productions.length; i++) {
                    AutomationProduction production = context.config.automation.productions[i];

                    writer.write("static StackEntryPayload _reduction");
                    writer.write(String.valueOf(i));
                    writer.write("(std::vector<StackEntry>* _grx_stack) {");
                    writer.newLine();
                    writer.enterIndent();

                    writeReduceLambdaFor(writer, context, production);

                    writer.exitIndent();
                    writer.write('}');
                    writer.newLine();

                }

                // generate array of pointers to above functions corresponding to reductions
                writer.write("static constexpr StackEntryPayload (*reductions[])(std::vector<StackEntry>*) = {");

                for (int i = 0;; i++) {
                    writer.write("_reduction" + i);
                    if (i == context.config.automation.productions.length - 1) {
                        break;
                    }
                    writer.write(", ");
                }

                writer.write("};");
                writer.newLine();

                break;
            default:
                assert false;
                break;
        }

    }

    private static void writeParseMethod(ProgramWriter writer, GeneratorContext context) throws IOException {

        switch (context.config.targetLanguage) {
            case JAVA:
                writer.beginIndentedBlock("public void parse(int tokenId, Object tokenPayload)");
                break;
            case CPP:
                writer.beginIndentedBlock("bool parse(int tokenId, StackEntryPayload tokenPayload)");
                break;
            default:
                assert false;
                break;
        }

        // method body

        writer.beginIndentedBlock("while (true)");

        switch (context.config.targetLanguage) {
            case JAVA:
                writer.addStatement("int action = actionTable[terminalCount * stack.peek().previousState + tokenId]");
                break;
            case CPP:
                writer.addStatement("int action = actionTable[TERMINAL_COUNT * stack.back().previousState + tokenId]");
                break;
            default:
                assert false;
                break;
        }

        // if action is an error
        writer.beginIndentedBlock("if (action == 0)");
        switch (context.config.targetLanguage) {
            case JAVA:
                writer.addStatement("throw new RuntimeException(\"Syntax error\")");
                break;
            case CPP:
                writer.addStatement("return false");
                break;
            default:
                assert false;
                break;
        }
        writer.endIndentedBlock();

        // if action is accept
        writer.beginIndentedBlock("if (action == -1)");
        switch (context.config.targetLanguage) {
            case JAVA:
                writer.addStatement("payload = stack.peek().payload");
                writer.addStatement("return");
                break;
            case CPP:
                writer.addStatement("payload = stack.back().payload");
                writer.addStatement("payloadInitialized = true");
                writer.addStatement("return true");
                break;
            default:
                assert false;
                break;
        }
        writer.endIndentedBlock();

        // if action is shift
        writer.beginIndentedBlock("if (action > 0)");
        switch (context.config.targetLanguage) {
            case JAVA:
                writer.addStatement("stack.push(new StackEntry(action - 1, tokenPayload))");
                writer.addStatement("return");
                break;
            case CPP:
                writer.addStatement("stack.emplace_back(action - 1, tokenPayload)");
                writer.addStatement("return true");
                break;
            default:
                assert false;
                break;
        }
        writer.endIndentedBlock();

        // otherwise the action is reduce
        switch (context.config.targetLanguage) {
            case JAVA:
                writer.addStatement("int productionIndex = -action - 2");
                writer.addStatement("Object reducedProduction = reductions[productionIndex].reduce(stack)");
                writer.addStatement("StackEntry newState = stack.peek()");
                writer.addStatement("int nextState = gotoTable[newState.previousState * nonTerminalCount + productionLabels[productionIndex]]");
                writer.addStatement("stack.push(new StackEntry(nextState - 1, reducedProduction))");
                break;
            case CPP:
                writer.addStatement("const int productionIndex = -action - 2");
                writer.addStatement("const StackEntryPayload reducedProduction = reductions[productionIndex](&stack)");
                writer.addStatement("const StackEntry& newState = stack.back()");
                writer.addStatement("const int nextState = gotoTable[newState.previousState * NON_TERMINAL_COUNT + productionLabels[productionIndex]]");
                writer.addStatement("stack.emplace_back(nextState - 1, reducedProduction)");
                break;
            default:
                assert false;
                break;
        }

        // end of the outer infinite loop
        writer.endIndentedBlock();

        writer.endIndentedBlock();

    }

    private static void writeConstructor(ProgramWriter writer, GeneratorContext context) throws IOException {

        if (context.config.targetLanguage == TargetLanguage.JAVA) {
            writer.write("public ");
        }

        writer.write(context.config.getName());
        writer.write("() ");

        writer.beginIndentedBlock();

        switch (context.config.targetLanguage) {
            case JAVA:
                writer.addStatement("stack = new Stack<>()");
                writer.addStatement("stack.push(initialStackEntry)");
                break;
            case CPP:
                writer.addStatement("stack.emplace_back()");
                break;
            default:
                assert false;
                break;
        }

        writer.endIndentedBlock();

    }

    private static void writeResetMethod(ProgramWriter writer, TargetLanguage targetLanguage) throws IOException {

        switch (targetLanguage) {

            case JAVA:
                writer.beginIndentedBlock("public void reset()");

                writer.addStatement("stack.clear()");
                writer.addStatement("stack.push(initialStackEntry)");
                writer.addStatement("payload = null");

                break;

            case CPP:
                writer.beginIndentedBlock("void reset()");

                writer.addStatement("stack.clear()");
                writer.addStatement("stack.emplace_back()");
                writer.addStatement("payloadInitialized = false");

                break;

            default:
                assert false;
                break;
        }

        writer.endIndentedBlock();

    }

    private static void writeSuccessfullyParsedMethod(ProgramWriter writer, TargetLanguage targetLanguage) throws IOException {

        switch (targetLanguage) {
            case JAVA:
                writer.beginIndentedBlock("public boolean successfullyParsed()");
                writer.addStatement("return payload != null");
                writer.endIndentedBlock();
                break;
            case CPP:
                writer.beginIndentedBlock("[[nodiscard]] bool successfullyParsed() const");
                writer.addStatement("return payloadInitialized");
                writer.endIndentedBlock();
                break;
            default:
                assert false;
                break;
        }

    }

    private static void writeGetValueMethod(ProgramWriter writer, TargetLanguage targetLanguage) throws IOException {

        switch (targetLanguage) {
            case JAVA:
                writer.beginIndentedBlock("public Object getValue()");
                writer.addStatement("assert payload != null : \"parsing did not succeed\"");
                writer.addStatement("return payload");
                writer.endIndentedBlock();
                break;
            case CPP:
                writer.beginIndentedBlock("[[nodiscard]] StackEntryPayload getValue() const");
                writer.addStatement("return payload");
                writer.endIndentedBlock();
                break;
            default:
                assert false;
                break;
        }

    }

    static void generate(ProgramWriter writer, GeneratorContext context) throws IOException {

        switch (context.config.targetLanguage) {
            case JAVA:
                writer.write("public final class ");
                break;
            case CPP:
                writer.write("class ");
                break;
            default:
                assert false;
                break;
        }

        writer.write(context.config.getName());
        writer.write(" {");
        writer.newLine();

        if (context.config.targetLanguage == TargetLanguage.CPP) {
            writer.write("private:");
            writer.newLine();
        }

        writer.enterIndent();

        // fields

        MetaGenerator.writeConstants(writer, context.config.automation, context.config.targetLanguage);

        MetaGenerator.writeGotoTable(writer, context.config.automation, context.config.targetLanguage);

        MetaGenerator.writeActionTable(writer, context.config.automation, context.config.targetLanguage);

        MetaGenerator.writeProductionLabelTable(writer, context.config.automation, context.config.targetLanguage);

        // inner classes

        switch (context.config.targetLanguage) {
            case JAVA:
                writeStackEntryJavaClass(writer);
                writeReductorJavaInterface(writer);
                break;
            case CPP:
                writeStackEntryPayloadCPPUnion(writer, context);
                writeStackEntryCPPStruct(writer);
                break;
            default:
                assert false;
                break;
        }

        writeReductionsArray(writer, context);

        // initialStackEntry
        switch (context.config.targetLanguage) {
            case JAVA:
                writer.write("private static final StackEntry initialStackEntry = new StackEntry(0, null);");
                break;
            case CPP:
                // for cpp, we create the initial stack entry inline and do not use any constant for it
                break;
            default:
                assert false;
                break;
        }
        writer.newLine();

        // stack
        switch (context.config.targetLanguage) {
            case JAVA:
                writer.write("private Stack<StackEntry> stack;");
                break;
            case CPP:
                writer.write("std::vector<StackEntry> stack;");
                break;
            default:
                assert false;
                break;
        }
        writer.newLine();

        // payload
        switch (context.config.targetLanguage) {
            case JAVA:
                writer.write("private Object payload = null;");
                writer.newLine();
                break;
            case CPP:
                writer.write("StackEntryPayload payload;");
                writer.newLine();
                writer.write("bool payloadInitialized = false;");
                writer.newLine();
                break;
            default:
                assert false;
                break;
        }

        // public methods
        if (context.config.targetLanguage == TargetLanguage.CPP) {
            writer.cancelIndentationForPresentLine();
            writer.write("public:");
            writer.newLine();
        }

        writeConstructor(writer, context);

        writeResetMethod(writer, context.config.targetLanguage);

        writeParseMethod(writer, context);

        writeSuccessfullyParsedMethod(writer, context.config.targetLanguage);

        writeGetValueMethod(writer, context.config.targetLanguage);

        writer.exitIndent();

        switch (context.config.targetLanguage) {
            case JAVA:
                writer.write("}");
                break;
            case CPP:
                writer.write("};");
                break;
            default:
                assert false;
                break;
        }

    }

}