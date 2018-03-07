package com.cool.config;

/**
 * Created by codelover on 17/6/18.
 * 实现这个接口，当spring context初始化完成后，严格来说是在ContextRefreshedEvent事件产生后会调用接口的init做初始化操作
 */
public interface Initializer {
    /**
     * 在产生{@link org.springframework.context.event.ContextRefreshedEvent}时，会调用这个方法，进行Spring Context刷新后的初始化
     */
    void init();
}
