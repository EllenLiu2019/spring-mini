package com.minis.boot.context.config;

import com.minis.boot.env.PropertySourceLoader;
import com.minis.boot.env.YamlPropertySourceLoader;
import com.minis.core.env.PropertySource;

import java.io.IOException;
import java.util.List;

public class StandardConfigDataLoader implements ConfigDataLoader {
    private final PropertySourceLoader propertySourceLoader = new YamlPropertySourceLoader();

    @Override
    public ConfigData load() throws IOException {
        List<PropertySource<?>> propertySources = this.propertySourceLoader.load();
        return new ConfigData(propertySources);
    }


}
