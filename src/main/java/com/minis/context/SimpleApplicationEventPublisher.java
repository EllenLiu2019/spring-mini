package com.minis.context;

import java.util.ArrayList;
import java.util.List;

public class SimpleApplicationEventPublisher implements ApplicationEventPublisher {
    List<ApplicationListener<ApplicationEvent>> listeners = new ArrayList<>();
    @Override
    public void publishEvent(ApplicationEvent event) {
        for(ApplicationListener<ApplicationEvent> listener: listeners) {
            listener.onApplicationEvent(event);
        }
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        this.listeners.add((ApplicationListener<ApplicationEvent>) listener);
    }
}
