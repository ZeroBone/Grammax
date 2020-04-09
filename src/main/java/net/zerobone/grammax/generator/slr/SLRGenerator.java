package net.zerobone.grammax.generator.slr;

import com.squareup.javapoet.JavaFile;
import net.zerobone.grammax.generator.GeneratorContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SLRGenerator {

    public static void generate(GeneratorContext context) throws IOException {
        {
            JavaFile javaFile = JavaFile
                .builder(context.packageName, SLRParserClassGenerator.generate(context).build())
                .indent("    ")
                .build();

            BufferedWriter writer = new BufferedWriter(new FileWriter(context.className + ".java"));
            javaFile.writeTo(writer);
            writer.close();
        }
    }

}