package com.minis.boot.context.config;

import com.minis.boot.SpringApplication;
import com.minis.boot.env.EnvironmentPostProcessor;
import com.minis.core.env.ConfigurableEnvironment;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
public class ConfigDataEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        log.trace("Post-processing environment to add config data");
        getConfigDataEnvironment(environment, application.getAdditionalProfiles()).processAndApply();
    }

    ConfigDataEnvironment getConfigDataEnvironment(ConfigurableEnvironment environment, Collection<String> additionalProfiles) {
        return new ConfigDataEnvironment(environment, additionalProfiles);
    }

}
