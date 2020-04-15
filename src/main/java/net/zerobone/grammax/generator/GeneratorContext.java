package net.zerobone.grammax.generator;

import net.zerobone.grammax.GrammaxConfiguration;
import net.zerobone.grammax.grammar.automation.Automation;

public class GeneratorContext {

    public final String className;

    public final String packageName;

    public final Automation automation;

    public final GrammaxConfiguration configuration;

    public GeneratorContext(String className, String packageName, Automation automation, GrammaxConfiguration configuration) {
        this.className = className;
        this.packageName = packageName;
        this.automation = automation;
        this.configuration = configuration;
    }

}