package net.zerobone.grammax.generator;

import net.zerobone.grammax.GrammaxConfiguration;
import net.zerobone.grammax.grammar.automation.Automation;

public class GeneratorContext {

    public final Automation automation;

    public final GrammaxConfiguration configuration;

    public GeneratorContext(Automation automation, GrammaxConfiguration configuration) {
        this.automation = automation;
        this.configuration = configuration;
    }

}