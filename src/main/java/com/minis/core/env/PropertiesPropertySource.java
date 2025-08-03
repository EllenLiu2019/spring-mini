package com.minis.core.env;

import java.util.Map;
import java.util.Properties;

public class PropertiesPropertySource extends MapPropertySource {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public PropertiesPropertySource(String name, Properties source) {
        super(name, (Map) source);
    }

    protected PropertiesPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }


    @Override
    public String[] getPropertyNames() {
        synchronized (this.source) {
            return ((Map<?, ?>) this.source).keySet().stream().filter(k -> k instanceof String).toArray(String[]::new);
        }
    }
}
