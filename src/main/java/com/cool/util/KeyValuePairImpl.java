package com.cool.util;

/**
 * Created by codelover on 17/3/28.
 * 键值对类
 */
public class KeyValuePairImpl<K,V> implements KeyValuePair<K,V> {
    private K key;
    private V value;

    public KeyValuePairImpl() {
    }

    public KeyValuePairImpl(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
