package com.minis.boot.context.config;

import com.minis.core.env.ConfigurableEnvironment;
import com.minis.core.env.MutablePropertySources;
import com.minis.core.env.PropertySource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;

@Slf4j
public class ConfigDataEnvironment {

    private final ConfigurableEnvironment environment;

    private final Collection<String> additionalProfiles;

    private final ConfigDataLoader loader;

    ConfigDataEnvironment(ConfigurableEnvironment environment, Collection<String> additionalProfiles) {
        this.environment = environment;
        this.additionalProfiles = additionalProfiles;
        this.loader = new StandardConfigDataLoader();
    }

    void processAndApply() {
        try {
            ConfigData configData = this.loader.load();
            this.applyToEnvironment(configData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void applyToEnvironment(ConfigData configData) {
        MutablePropertySources propertySources = this.environment.getPropertySources();
        applyPropertySources(configData, propertySources);
    }

    private void applyPropertySources(ConfigData configData, MutablePropertySources propertySources) {
        for (PropertySource<?> propertySource : configData.getPropertySources()) {
            log.trace("Adding property source [{}] to environment", propertySource.getName());
            propertySources.addLast(propertySource);
        }
    }
}
