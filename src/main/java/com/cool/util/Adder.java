package com.cool.util;

import java.util.Collection;

/**
 * Created by codelover on 17/5/24.
 * 内陪有维护一个集合的,需要添加元素的
 */
public interface Adder<E> {
    void add(E e);
    void addAll(E... e);
    void addAll(Iterable<E> ts);
    void addAll(Collection<? extends E> collection);

}
