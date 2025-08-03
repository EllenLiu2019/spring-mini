package com.minis.boot.env;

import com.minis.boot.SpringApplication;
import com.minis.core.env.ConfigurableEnvironment;

@FunctionalInterface
public interface EnvironmentPostProcessor {

    /**
     * Post-process the given environment.
     * @param environment the environment to post-process
     * @param application the application to which the environment belongs
     */
    void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application);
}
