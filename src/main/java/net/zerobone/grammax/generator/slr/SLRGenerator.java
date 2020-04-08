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
                .builder(context.packageName, SLRParserGenerator.generate(context).build())
                .indent("\t")
                .build();

            BufferedWriter writer = new BufferedWriter(new FileWriter("Parser.java"));
            javaFile.writeTo(writer);
            writer.close();
        }
    }

}