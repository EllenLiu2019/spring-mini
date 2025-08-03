package com.minis.boot.context.event;

import com.minis.boot.SpringApplication;
import com.minis.boot.SpringApplicationRunListener;
import com.minis.context.event.SimpleApplicationEventMulticaster;
import com.minis.core.env.ConfigurableEnvironment;

public class EventPublishingRunListener implements SpringApplicationRunListener {

    private final SpringApplication application;

    private final SimpleApplicationEventMulticaster initialMulticaster;

    public EventPublishingRunListener(SpringApplication application) {
        this.application = application;
        this.initialMulticaster = new SimpleApplicationEventMulticaster();
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        this.initialMulticaster.multicastEvent(new ApplicationEnvironmentPreparedEvent(this.application, environment));
    }
}
