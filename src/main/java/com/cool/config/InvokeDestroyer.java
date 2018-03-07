package com.cool.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by codelover on 17/6/19.
 */
@Component
public class InvokeDestroyer implements ApplicationListener<ContextClosedEvent> {

    @Autowired(required = false)
    private List<Destroyer> list;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (list == null) {
            return;
        }
        for (Destroyer d :
                list) {
            d.doDestroy();
        }
    }
}
