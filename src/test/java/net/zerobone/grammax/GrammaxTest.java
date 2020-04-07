package net.zerobone.grammax;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class GrammaxTest {

    @BeforeAll
    public void setUp() {
        System.out.println("setUp");
    }

    @AfterAll
    public void tearDown() {
        System.out.println("tearDown");
    }

    @Test
    public void testSomething() {

        Assertions.assertEquals(1, 1);

    }

    @Test
    public void testSomethingElse() {

        int v = 2 + 2;

        Assertions.assertEquals(4, v);

    }

}