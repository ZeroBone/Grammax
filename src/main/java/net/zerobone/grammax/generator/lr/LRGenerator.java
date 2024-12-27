package net.zerobone.grammax.generator.lr;

import net.zerobone.grammax.Grammax;
import net.zerobone.grammax.TargetLanguage;
import net.zerobone.grammax.generator.GeneratorContext;
import net.zerobone.grammax.generator.ProgramWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LRGenerator {

    private static void writeHeader(ProgramWriter writer) throws IOException {
        writer.write("/* This file was generated by Grammax v");
        writer.write(Grammax.VERSION);
        writer.write(" */");
        writer.newLine();
        writer.newLine();
    }

    private static void generateJavaParserClass(GeneratorContext context) throws IOException {

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(context.config.getName() + ".java"));

        ProgramWriter writer = new ProgramWriter(bufferedWriter);

        writeHeader(writer);

        if (context.config.topCode != null) {
            writer.writeAlignTrim(context.config.topCode);
        }

        writer.newLine();

        // local imports
        writer.write("import java.util.Stack;");

        writer.newLine();
        writer.newLine();

        LRParserClassGenerator.generate(writer, context);

        writer.close();

    }

    private static void generateCppHeader(GeneratorContext context) throws IOException {

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(context.config.getName() + ".h"));
        ProgramWriter writer = new ProgramWriter(bufferedWriter);

        writeHeader(writer);

        String guardDefineVariableName = "GRAMMAX_PARSER_" + context.config.getName().toUpperCase() + "_H";

        writer.write("#ifndef ");
        writer.write(guardDefineVariableName);
        writer.newLine();

        writer.write("#define ");
        writer.write(guardDefineVariableName);
        writer.newLine();

        if (context.config.topCode != null) {
            writer.writeAlignTrim(context.config.topCode);
        }

        writer.newLine();

        LRParserClassGenerator.generate(writer, context);

        writer.newLine();
        writer.newLine();
        writer.write("#endif");

        writer.close();

    }

    public static void generate(GeneratorContext context) throws IOException {

        TargetLanguage targetLanguage = context.config.targetLanguage;

        switch (targetLanguage) {

            case JAVA:
                generateJavaParserClass(context);
                break;

            case CPP:
                generateCppHeader(context);
                break;

            default:
                assert false;
                break;

        }

    }

}