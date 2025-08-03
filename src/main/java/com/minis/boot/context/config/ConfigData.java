package com.minis.boot.context.config;

import com.minis.core.env.PropertySource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ConfigData {

    private final List<PropertySource<?>> propertySources;

    public static final ConfigData EMPTY = new ConfigData(Collections.emptySet());

    public ConfigData(Collection<? extends PropertySource<?>> propertySources) {
        this.propertySources = List.copyOf(propertySources);
    }

    public List<PropertySource<?>> getPropertySources() {
        return this.propertySources;
    }
}
