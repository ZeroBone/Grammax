package net.zerobone.grammax.generator;

import net.zerobone.grammax.grammar.automation.Automation;

public class GeneratorContext {

    public final String packageName;

    public final Automation automation;

    public GeneratorContext(String packageName, Automation automation) {
        this.packageName = packageName;
        this.automation = automation;
    }

}