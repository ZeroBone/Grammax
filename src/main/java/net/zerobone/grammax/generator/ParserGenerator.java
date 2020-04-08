package net.zerobone.grammax.generator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

class ParserGenerator {

    private ParserGenerator() {}

    static TypeSpec.Builder generate(GeneratorContext context) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("Parser")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // fields

        MetaGenerator.writeConstants(context.automation, classBuilder);

        classBuilder.addField(MetaGenerator.constructGotoTable(context.automation));

        classBuilder.addField(MetaGenerator.constructActionTable(context.automation));

        classBuilder.addField(MetaGenerator.constructProductionTable(context.automation));

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