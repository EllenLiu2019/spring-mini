package com.minis.context.event;

import com.minis.boot.env.EnvironmentPostProcessorApplicationListener;

import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleApplicationEventMulticaster implements ApplicationEventMulticaster {

    public volatile Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

    public SimpleApplicationEventMulticaster() {
        this.applicationListeners.add(new EnvironmentPostProcessorApplicationListener());
    }

    @Override
    public void multicastEvent(ApplicationEvent event) {
        for (ApplicationListener listener : applicationListeners) {
            listener.onApplicationEvent(event);
        }
    }
}
