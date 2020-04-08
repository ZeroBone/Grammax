package net.zerobone.grammax.generator;

import net.zerobone.grammax.grammar.automation.Automation;

public class GeneratorContext {

    final String packageName;

    final Automation automation;

    public GeneratorContext(String packageName, Automation automation) {
        this.packageName = packageName;
        this.automation = automation;
    }

}