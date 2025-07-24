package com.minis.context;

import com.minis.context.event.ApplicationEvent;
import com.minis.context.event.ApplicationListener;

import java.util.ArrayList;
import java.util.List;

public class SimpleApplicationEventPublisher implements ApplicationEventPublisher {
    List<ApplicationListener<? extends ApplicationEvent>> listeners = new ArrayList<>();

    @Override
    public void publishEvent(ApplicationEvent event) {
        for (ApplicationListener listener : listeners) {
            listener.onApplicationEvent(event);
        }
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        this.listeners.add(listener);
    }
}
