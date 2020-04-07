package net.zerobone.grammax.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class BijectiveMapTest {

    @Test
    public void smokeTest() {

        BijectiveMap<Integer, Integer> map = new BijectiveMap<>();

        Assertions.assertEquals(0, map.size());

        Assertions.assertTrue(map.keys().isEmpty());

        Assertions.assertTrue(map.values().isEmpty());

    }

    @Test
    public void retrieveTest() {

        BijectiveMap<Integer, Integer> map = new BijectiveMap<>();

        map.put(3, 4);
        Assertions.assertEquals(1, map.size());
        map.put(1, 2);
        Assertions.assertEquals(2, map.size());
        map.put(2, 3);
        Assertions.assertEquals(3, map.size());

        Assertions.assertEquals(3, (int)map.mapKey(2));
        Assertions.assertEquals(2, (int)map.mapKey(1));
        Assertions.assertEquals(4, (int)map.mapKey(3));

        Assertions.assertEquals(1, (int)map.mapValue(2));
        Assertions.assertEquals(2, (int)map.mapValue(3));
        Assertions.assertEquals(3, (int)map.mapValue(4));

        Assertions.assertTrue(map.containsKey(1));
        Assertions.assertTrue(map.containsKey(2));
        Assertions.assertTrue(map.containsKey(3));
        Assertions.assertFalse(map.containsKey(0));
        Assertions.assertFalse(map.containsKey(4));

        Assertions.assertTrue(map.containsValue(2));
        Assertions.assertTrue(map.containsValue(3));
        Assertions.assertTrue(map.containsValue(4));
        Assertions.assertFalse(map.containsValue(1));
        Assertions.assertFalse(map.containsValue(5));

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

            Assertions.assertEquals(count + 1, map.size());

        }

        for (int i = values.length - 1; i >= 0; i--) {

            int value = values[i];

            Assertions.assertEquals(i, (int)map.mapValue(value));
            Assertions.assertEquals(value, (int)map.mapKey(i));

        }

    }

}