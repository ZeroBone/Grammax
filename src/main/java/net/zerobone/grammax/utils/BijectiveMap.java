package net.zerobone.grammax.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class BijectiveMap<K, V> {

    private HashMap<K, V> keyToValue = new HashMap<>();
    private HashMap<V, K> valueToKey = new HashMap<>();

    public BijectiveMap() {}

    public void put(K key, V value) {
        keyToValue.put(key, value);
        valueToKey.put(value, key);
    }

    public void removeByKey(K key) {

        V value = keyToValue.get(key);

        if (value == null) {
            return;
        }

        valueToKey.remove(value);

        keyToValue.remove(key);

    }

    public void removeByValue(V value) {

        K key = valueToKey.get(value);

        if (key == null) {
            return;
        }

        keyToValue.remove(key);

        valueToKey.remove(value);

    }

    public V mapKey(K key) {
        return keyToValue.get(key);
    }

    public K mapValue(V value) {
        return valueToKey.get(value);
    }

    public boolean containsKey(K key) {
        return keyToValue.containsKey(key);
    }

    public boolean containsValue(V value) {
        return valueToKey.containsKey(value);
    }

    public Collection<V> values() {
        return keyToValue.values();
    }

    public Collection<K> keys() {
        return valueToKey.values();
    }

    public Set<HashMap.Entry<K, V>> keyValueEntrySet() {
        return keyToValue.entrySet();
    }

    public int size() {
        return keyToValue.size();
    }

}