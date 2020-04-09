package net.zerobone.grammax.generator.slr;

import com.squareup.javapoet.*;
import net.zerobone.grammax.generator.GeneratorContext;
import net.zerobone.grammax.generator.MetaGenerator;
import net.zerobone.grammax.grammar.automation.AutomationProduction;
import net.zerobone.grammax.grammar.automation.AutomationSymbol;

import javax.lang.model.element.Modifier;

class SLRParserClassGenerator {

    private SLRParserClassGenerator() {}

    private static TypeSpec generateStackEntryClass() {

        TypeSpec.Builder builder = TypeSpec.classBuilder("StackEntry");

        builder.addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

        // fields

        builder.addField(int.class, "previousState", Modifier.PRIVATE, Modifier.FINAL);

        builder.addField(Object.class, "payload", Modifier.PRIVATE, Modifier.FINAL);

        // constructor

        builder.addMethod(
            MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(int.class, "previousState")
                .addParameter(Object.class, "payload")
                .addStatement("this.previousState = previousState")
                .addStatement("this.payload = payload")
                .build()
        );

        return builder.build();

    }

    private static TypeName getStackType(GeneratorContext context) {
        return ParameterizedTypeName.get(
            ClassName.get("java.util", "Stack"),
            getStackEntryType(context)
        );
    }

    private static TypeName getStackEntryType(GeneratorContext context) {
        return context.getClassName().nestedClass("StackEntry");
    }

    private static TypeSpec generateReductorInterface(GeneratorContext context) {

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder("Reductor");

        builder.addModifiers(Modifier.PRIVATE, Modifier.STATIC);

        // constructor

        builder.addMethod(
            MethodSpec
                .methodBuilder("reduce")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(getStackType(context), "_grx_stack")
                .returns(Object.class)
                .build()
        );

        return builder.build();

    }

    private static MethodSpec generateReduceLambdaFor(GeneratorContext context, AutomationProduction production) {

        MethodSpec.Builder builder = MethodSpec.methodBuilder("reduce");

        builder.addAnnotation(Override.class);

        builder.addModifiers(Modifier.PUBLIC);

        builder.addParameter(getStackType(context), "_grx_stack");

        builder.returns(Object.class);

        if (production.code == null) {
            for (AutomationSymbol ignored : production.body) {
                builder.addStatement("_grx_stack.pop()");
            }
            builder.addStatement("return null");
            return builder.build();
        }

        for (int i = production.body.length - 1; i >= 0; i--) {

            AutomationSymbol symbol = production.body[i];

            if (symbol.argumentName == null) {
                builder.addStatement("_grx_stack.pop()");
                continue;
            }

            builder.addStatement("Object " + symbol.argumentName + " = _grx_stack.pop().payload");

        }

        builder.addStatement("Object v");

        builder.addCode("{$L}\n", production.code);

        builder.addStatement("return v");

        return builder.build();

    }

    private static TypeSpec generateReductorFor(GeneratorContext context, AutomationProduction production) {

        TypeSpec.Builder reductor = TypeSpec.anonymousClassBuilder("");

        reductor.addSuperinterface(context.getClassName().nestedClass("Reductor"));

        reductor.addMethod(generateReduceLambdaFor(context, production));

        return reductor.build();

    }

    private static FieldSpec generateReductionsArray(GeneratorContext context) {

        assert context.automation.productions.length != 0;

        StringBuilder maskSb = new StringBuilder();

        maskSb.append('{');

        TypeSpec[] reductors = new TypeSpec[context.automation.productions.length];

        for (int i = 0;;i++) {

            AutomationProduction production = context.automation.productions[i];

            reductors[i] = generateReductorFor(context, production);

            maskSb.append("$L");

            if (i == context.automation.productions.length - 1) {
                break;
            }

            maskSb.append(',');

        }

        maskSb.append('}');

        final ClassName reductor = context.getClassName().nestedClass("Reductor");

        //noinspection ConfusingArgumentToVarargsMethod
        return FieldSpec.builder(ArrayTypeName.of(reductor), "reductions")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .addAnnotation(
                // @SuppressWarnings("Convert2Lambda")
                AnnotationSpec.builder(SuppressWarnings.class)
                    .addMember("value", "$S", "Convert2Lambda")
                    .build()
            )
            .initializer(maskSb.toString(), reductors)
            .build();

    }

    private static MethodSpec generateParseMethod() {

        MethodSpec.Builder builder = MethodSpec.methodBuilder("parse");

        builder.addModifiers(Modifier.PUBLIC);

        builder.addParameter(int.class, "tokenId");

        builder.addParameter(Object.class, "tokenPayload");

        // method body

        builder.beginControlFlow("while (true)");

        builder.addStatement("int action = actionTable[terminalCount * stack.peek().previousState + tokenId]");

        // if action is an error
        builder.beginControlFlow("if (action == 0)");
        builder.addStatement("throw new RuntimeException(\"Syntax error\")");
        builder.endControlFlow();

        // if action is accept
        builder.beginControlFlow("if (action == -1)");
        builder.addStatement("payload = stack.peek().payload");
        builder.addStatement("return");
        builder.endControlFlow();

        // if action is shift
        builder.beginControlFlow("if (action > 0)");
        builder.addStatement("stack.push(new StackEntry(action, tokenPayload))");
        builder.addStatement("return");
        builder.endControlFlow();

        // otherwise the action is reduce

        builder.addStatement("int productionIndex = -action - 2");
        builder.addStatement("Object reducedProduction = reductions[productionIndex].reduce(stack)");
        builder.addStatement("StackEntry newState = stack.peek()");
        builder.addStatement("int nextState = gotoTable[newState.previousState * nonTerminalCount + productionLabels[productionIndex]]");
        builder.addStatement("stack.push(new StackEntry(nextState, reducedProduction))");

        // end of the outer infinite loop
        builder.endControlFlow();

        return builder.build();

    }

    static TypeSpec.Builder generate(GeneratorContext context) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(context.className)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // fields

        MetaGenerator.writeConstants(context.automation, classBuilder);

        classBuilder.addField(MetaGenerator.constructGotoTable(context.automation));

        classBuilder.addField(MetaGenerator.constructActionTable(context.automation));

        classBuilder.addField(MetaGenerator.constructProductionLabelTable(context.automation));

        classBuilder.addField(generateReductionsArray(context));

        classBuilder.addField(
            FieldSpec.builder(getStackEntryType(context), "initialStackEntry")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new StackEntry(0, null)")
                .build()
        );

        classBuilder.addField(
            FieldSpec.builder(getStackType(context), "stack", Modifier.PRIVATE)
                .build()
        );

        classBuilder.addField(
            FieldSpec.builder(Object.class, "payload", Modifier.PRIVATE)
                .initializer("null")
                .build()
        );

        // inner classes

        classBuilder.addType(generateStackEntryClass());

        classBuilder.addType(generateReductorInterface(context));

        // methods

        classBuilder.addMethod(
            MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("stack = new Stack<>()")
                .addStatement("stack.push(initialStackEntry)")
                .build()
        );

        classBuilder.addMethod(
            MethodSpec
                .methodBuilder("reset")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("stack.clear()")
                .addStatement("stack.push(initialStackEntry)")
                .addStatement("payload = null")
                .build()
        );

        classBuilder.addMethod(generateParseMethod());

        classBuilder.addMethod(
            MethodSpec
                .methodBuilder("successfullyParsed")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return payload != null")
                .returns(boolean.class)
                .build()
        );

        classBuilder.addMethod(
            MethodSpec
                .methodBuilder("getValue")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("assert payload != null : \"parsing did not succeed\"")
                .addStatement("return payload")
                .returns(Object.class)
                .build()
        );

        return classBuilder;

    }

}