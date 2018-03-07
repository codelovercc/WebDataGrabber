package com.cool.util;

/**
 * Created by codelover on 17/4/11.
 *
 */
public interface KeyValuePair<K,V> {
    K getKey();
    void setKey(K key);
    V getValue();
    void setValue(V value);
    static <K,V> KeyValuePair<K,V> getInstance(K key, V value){
        return new KeyValuePairImpl<K, V>(key, value);
    }
    static <K,V> KeyValuePair<K,V> getInstance(){
        return new KeyValuePairImpl<K, V>();
    }
}
