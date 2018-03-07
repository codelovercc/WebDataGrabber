package com.cool.config;

/**
 * Created by codelover on 17/6/19.
 * 实现这个接口，将在应用程序关闭或者spring context被关闭后调用
 */
public interface Destroyer {
    void doDestroy();
}
