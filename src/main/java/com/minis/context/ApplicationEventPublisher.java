package com.minis.context;

import com.minis.context.event.ApplicationEvent;
import com.minis.context.event.ApplicationListener;

public interface ApplicationEventPublisher {
    void publishEvent(ApplicationEvent event);
    void addApplicationListener(ApplicationListener<?> listener);
}
