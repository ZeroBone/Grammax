package net.zerobone.grammax.grammar.slr;

import net.zerobone.grammax.grammar.GrammarBuilder;
import org.junit.jupiter.api.Test;

public class SLRTests {

    @Test
    public void postfixTest() {

        GrammarBuilder builder = new GrammarBuilder();

        builder.production(1, "s", "s", "s", "PLUS");
        builder.production(2, "s", "s", "s", "MUL");
        builder.production(3, "s", "A");

        SLRTestCase testCase = new SLRTestCase(builder);

        testCase.expectAction(0, "A", "s2");

        testCase.expectAction(1, "A", "s2");
        testCase.expectAction(1, "$", "accept");

        testCase.expectAction(2, "A", "r3");
        testCase.expectAction(2, "PLUS", "r3");
        testCase.expectAction(2, "MUL", "r3");
        testCase.expectAction(2, "$", "r3");

        testCase.expectAction(3, "PLUS", "s4");
        testCase.expectAction(3, "MUL", "s5");

        testCase.expectAction(4, "A", "r1");
        testCase.expectAction(4, "PLUS", "r1");
        testCase.expectAction(4, "MUL", "r1");
        testCase.expectAction(4, "$", "r1");

        testCase.expectAction(5, "A", "r2");
        testCase.expectAction(5, "PLUS", "r2");
        testCase.expectAction(5, "MUL", "r2");
        testCase.expectAction(5, "$", "r2");

        testCase.test();

    }

}