package ru.job4j.cache;

import  java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCache<K, V> {

    private final Map<K, SoftReference<V>> cache = new HashMap<>();

    public final void put(K key, V value) {
        if (!cache.containsKey(key) || cache.get(key).get() == null) {
            cache.put(key, new SoftReference<>(value));
        }
    }

    public final V get(K key) {
        put(key, load(key));
        return cache.get(key).get();
    }

    protected abstract V load(K key);

}