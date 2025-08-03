package com.minis.boot.context.event;

import com.minis.boot.SpringApplication;
import com.minis.context.event.ApplicationEvent;
import com.minis.core.env.ConfigurableEnvironment;

public class ApplicationEnvironmentPreparedEvent extends ApplicationEvent {

    private final ConfigurableEnvironment environment;

    public ApplicationEnvironmentPreparedEvent(SpringApplication application, ConfigurableEnvironment environment) {
        super(application);
        this.environment = environment;
    }

    public SpringApplication getSpringApplication() {
        return (SpringApplication) getSource();
    }

    public ConfigurableEnvironment getEnvironment() {
        return this.environment;
    }
}
