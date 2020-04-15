package net.zerobone.grammax.generator;

import net.zerobone.grammax.GrammaxConfiguration;
import net.zerobone.grammax.grammar.automation.Automation;

public class GeneratorContext {

    public final String className;

    public final Automation automation;

    public final GrammaxConfiguration configuration;

    public GeneratorContext(String className, Automation automation, GrammaxConfiguration configuration) {
        this.className = className;
        this.automation = automation;
        this.configuration = configuration;
    }

}