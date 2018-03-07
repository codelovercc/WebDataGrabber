package com.cool.util;

import java.util.Observer;

/**
 * Created by codelover on 17/4/22.
 * 把{@link java.util.Observable}提取出来做成了这个接口,实现这个接口必须要继承类{@link java.util.Observable}
 */
public interface Observable {
    void addObserver(Observer o);

    void deleteObserver(Observer o);

    void notifyObservers();

    void notifyObservers(Object arg);

    void deleteObservers();

    boolean hasChanged();

    int countObservers();

    default void setChanged() {
        //do nothing
    }
}
