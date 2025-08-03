package com.minis.boot.env;

import com.minis.boot.SpringApplication;
import com.minis.boot.context.config.ConfigDataEnvironmentPostProcessor;
import com.minis.boot.context.event.ApplicationEnvironmentPreparedEvent;
import com.minis.context.event.ApplicationEvent;
import com.minis.context.event.ApplicationListener;
import com.minis.core.env.ConfigurableEnvironment;

import java.util.List;

public class EnvironmentPostProcessorApplicationListener implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent environmentPreparedEvent) {
            onApplicationEnvironmentPreparedEvent(environmentPreparedEvent);
        }
    }

    private void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        SpringApplication application = event.getSpringApplication();
        List<EnvironmentPostProcessor> postProcessors = getEnvironmentPostProcessors();
        for (EnvironmentPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessEnvironment(environment, application);
        }
    }

    List<EnvironmentPostProcessor> getEnvironmentPostProcessors() {
        ConfigDataEnvironmentPostProcessor configDataEnvironmentPostProcessor = new ConfigDataEnvironmentPostProcessor();
        return List.of(configDataEnvironmentPostProcessor);
    }
}
