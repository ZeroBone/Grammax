package net.zerobone.grammax.generator;

import com.squareup.javapoet.ClassName;
import net.zerobone.grammax.grammar.automation.Automation;

public class GeneratorContext {

    public final String className;

    public final String packageName;

    public final Automation automation;

    public GeneratorContext(String className, String packageName, Automation automation) {
        this.className = className;
        this.packageName = packageName;
        this.automation = automation;
    }

    public ClassName getClassName() {
        return ClassName.get(packageName,"Parser");
    }

}