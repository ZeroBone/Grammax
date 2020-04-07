package net.zerobone.grammax;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class GrammaxTest {

    @Before
    public void setUp() {
        System.out.println("setUp");
    }

    @After
    public void tearDown() {
        System.out.println("tearDown");
    }

    @Test
    public void testSomething() {

        assertEquals(1, 1);

    }

    @Test
    public void testSomethingElse() {

        assertEquals(4, 2 + 2);

    }

}