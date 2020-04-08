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

    private static TypeSpec generateReductorInterface() {

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder("Reductor");

        builder.addModifiers(Modifier.PRIVATE, Modifier.STATIC);

        // constructor

        builder.addMethod(
            MethodSpec
                .methodBuilder("reduce")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(Object.class)
                .build()
        );

        return builder.build();

    }

    private static MethodSpec generateReduceLambdaFor(GeneratorContext context, AutomationProduction production) {

        TypeName stackType = ParameterizedTypeName.get(
            ClassName.get("java.util", "Stack"),
            context.getClassName().nestedClass("StackEntry")
        );

        MethodSpec.Builder builder = MethodSpec.methodBuilder("reduce");

        builder.addAnnotation(Override.class);

        builder.addModifiers(Modifier.PUBLIC);

        builder.addParameter(stackType, "_grx_stack");

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

            builder.addStatement("Object " + symbol.argumentName + " = _grx_stack.pop()");

        }

        builder.addStatement("return null");

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

        return FieldSpec.builder(ArrayTypeName.of(reductor), "reductions")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer(maskSb.toString(), reductors)
            .build();

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

        // inner classes

        classBuilder.addType(generateStackEntryClass());

        classBuilder.addType(generateReductorInterface());

        // methods

        classBuilder.addMethod(
            MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("System.out.println(0xff)")
                .build()
        );

        return classBuilder;

    }

}