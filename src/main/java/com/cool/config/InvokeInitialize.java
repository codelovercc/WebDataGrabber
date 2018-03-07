package com.cool.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by codelover on 17/6/18.
 * 用来调用实现了{@link Initializer}的bean进行context初始化后的初始化
 */
@Component
public class InvokeInitialize implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired(required = false)
    private List<Initializer> initializers;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (initializers == null) {
            return;
        }
        for (Initializer i :
                initializers) {
            i.init();
        }
    }
}
