package net.zerobone.grammax.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;

public class JavaWriter {

    private static final String INDENT = "    ";

    private final BufferedWriter writer;

    private boolean indenting = true;

    private int indent = 0;

    public JavaWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    private void indent() throws IOException {

        if (!indenting) {
            return;
        }

        indenting = false;

        for (int i = 0; i != indent; i++) {
            writer.write(INDENT);
        }

    }

    public void write(String s) throws IOException {
        indent();
        writer.write(s);
    }

    public void write(char c) throws IOException {
        indent();
        writer.write(c);
    }

    public void writeAlign(String s) throws IOException {

        BufferedReader bufReader = new BufferedReader(new StringReader(s));

        String line;
        while ((line=bufReader.readLine()) != null) {
            write(line);
            newLine();
        }

    }

    public void writeAlignTrim(String s) throws IOException {

        BufferedReader bufReader = new BufferedReader(new StringReader(s));

        String line;
        while ((line=bufReader.readLine()) != null) {
            write(line.trim());
            newLine();
        }

    }

    public void newLine() throws IOException {
        indenting = true;
        writer.newLine();
    }

    public void addStatement(String stmt) throws IOException {
        write(stmt);
        write(';');
        newLine();
    }

    public void enterIndent() {
        assert indent >= 0;
        indent++;
    }

    public void exitIndent() {
        indent--;
        assert indent >= 0;
    }

    public void beginIndentedBlock() throws IOException {
        write('{');
        newLine();
        enterIndent();
    }

    public void beginIndentedBlock(String title) throws IOException {
        write(title);
        write(" {");
        newLine();
        enterIndent();
    }

    public void endIndentedBlock() throws IOException {
        exitIndent();
        write('}');
        newLine();
    }

    public void close() throws IOException {
        writer.close();
    }

}