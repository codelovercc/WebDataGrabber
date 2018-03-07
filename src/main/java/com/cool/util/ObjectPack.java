package com.cool.util;

/**
 * 用于参数值外传的类,可以实现C# ref 关键字功能
 * @param <T> 包装的类
 */
public class ObjectPack<T> {
    private T object;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
