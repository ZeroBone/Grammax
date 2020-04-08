package net.zerobone.grammax.generator.slr;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.zerobone.grammax.generator.GeneratorContext;
import net.zerobone.grammax.generator.MetaGenerator;

import javax.lang.model.element.Modifier;

class SLRParserGenerator {

    private SLRParserGenerator() {}

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

        builder.addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

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

    static TypeSpec.Builder generate(GeneratorContext context) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("Parser")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // fields

        MetaGenerator.writeConstants(context.automation, classBuilder);

        classBuilder.addField(MetaGenerator.constructGotoTable(context.automation));

        classBuilder.addField(MetaGenerator.constructActionTable(context.automation));

        classBuilder.addField(MetaGenerator.constructProductionTable(context.automation));

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