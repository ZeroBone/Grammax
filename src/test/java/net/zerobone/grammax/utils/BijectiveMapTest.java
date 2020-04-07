package net.zerobone.grammax.utils;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class BijectiveMapTest {

    @Test
    public void smokeTest() {

        BijectiveMap<Integer, Integer> map = new BijectiveMap<>();

        assertEquals(0, map.size());

        assertTrue(map.keys().isEmpty());

        assertTrue(map.values().isEmpty());

    }

    @Test
    public void retrieveTest() {

        BijectiveMap<Integer, Integer> map = new BijectiveMap<>();

        map.put(3, 4);
        assertEquals(1, map.size());
        map.put(1, 2);
        assertEquals(2, map.size());
        map.put(2, 3);
        assertEquals(3, map.size());

        assertEquals(3, (int)map.mapKey(2));
        assertEquals(2, (int)map.mapKey(1));
        assertEquals(4, (int)map.mapKey(3));

        assertEquals(1, (int)map.mapValue(2));
        assertEquals(2, (int)map.mapValue(3));
        assertEquals(3, (int)map.mapValue(4));

        assertTrue(map.containsKey(1));
        assertTrue(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(0));
        assertFalse(map.containsKey(4));

        assertTrue(map.containsValue(2));
        assertTrue(map.containsValue(3));
        assertTrue(map.containsValue(4));
        assertFalse(map.containsValue(1));
        assertFalse(map.containsValue(5));

    }

    @Test
    public void randomTest() {

        int[] values = new int[100];
        int count = 0;

        BijectiveMap<Integer, Integer> map = new BijectiveMap<>();

        Random random = new Random();

        for (; count < values.length; count++) {

            values[count] = random.nextInt(1 << 30);

            map.put(count, values[count]);

            assertEquals(count + 1, map.size());

        }

        for (int i = values.length - 1; i >= 0; i--) {

            int value = values[i];

            assertEquals(i, (int)map.mapValue(value));
            assertEquals(value, (int)map.mapKey(i));

        }

    }

}