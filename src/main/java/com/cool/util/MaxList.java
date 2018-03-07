package com.cool.util;

import java.util.List;

/**
 * 可以指定最大条数的集合
 * @param <E> 保存的元素
 */
public interface MaxList<E> extends List<E> {
    int getMaxSize();

    void setMaxSize(int maxSize);

    boolean isMaxed();
}
