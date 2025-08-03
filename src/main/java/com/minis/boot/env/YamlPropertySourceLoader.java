package com.minis.boot.env;

import com.minis.beans.factory.config.YamlProcessor;
import com.minis.core.env.MapPropertySource;
import com.minis.core.env.PropertySource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class YamlPropertySourceLoader implements PropertySourceLoader {

    public List<PropertySource<?>> load() throws IOException {
        List<Map<String, Object>> loaded = new YamlProcessor().load();
        if (loaded.isEmpty()) {
            return Collections.emptyList();
        }
        List<PropertySource<?>> propertySources = new ArrayList<>(loaded.size());
        for (int i = 0; i < loaded.size(); i++) {
            String documentNumber = "Config source classpath:application.yml";
            propertySources.add(new MapPropertySource(documentNumber, Collections.unmodifiableMap(loaded.get(i))));
        }
        return propertySources;
    }

}
