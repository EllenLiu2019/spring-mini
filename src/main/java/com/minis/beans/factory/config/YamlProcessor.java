package com.minis.beans.factory.config;

import com.minis.core.io.ClassPathResource;
import com.minis.core.io.Resource;
import com.minis.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.BiConsumer;

@Slf4j
public class YamlProcessor {

    private final Resource[] resources;

    public YamlProcessor() {
        this.resources = new Resource[]{
                new ClassPathResource("/application.yml"),
                new ClassPathResource("/application.yaml")
        };
    }

    public List<Map<String, Object>> load() {
        List<Map<String, Object>> result = new ArrayList<>();
        process((properties, map) -> {
            log.debug("\n");
            log.debug("properties START ---");
            properties.forEach((key, value) -> log.debug("<" + key + ">: <" + value + ">"));
            log.debug("properties END ---\n");
            result.add(getFlattenedMap(map));
        });
        return result;
    }

    public void process(BiConsumer<Properties, Map<String, Object>> matchCallback) {
        Yaml yaml = new Yaml();
        for (Resource resource : this.resources) {
            boolean found = process(matchCallback, yaml, resource);
        }
    }

    private boolean process(BiConsumer<Properties, Map<String, Object>> callback, Yaml yaml, Resource resource) {
        boolean found = false;
        try (Reader reader = new UnicodeReader(resource.getInputStream())) {
            for (Object object : yaml.loadAll(reader)) {
                if (object != null && process(asMap(object), callback)) {
                    found = true;
                }
            }
        } catch (IOException ex) {
            log.warn(ex.getMessage());
            return found;
        }

        log.debug("Loaded document from YAML resource: " + resource);
        return found;
    }

    private boolean process(Map<String, Object> map, BiConsumer<Properties, Map<String, Object>> callback) {
        Properties properties = new Properties() {
            @Override
            public String getProperty(String key) {
                Object value = super.get(key);
                return (value != null ? value.toString() : null);
            }
        };
        properties.putAll(getFlattenedMap(map));

        log.debug("Merging document: " + map);
        callback.accept(properties, map);
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map<String, Object> asMap(Object object) {
        // YAML can have numbers as keys
        Map<String, Object> result = new LinkedHashMap<>();
        if (!(object instanceof Map map)) {
            // A document can be a text literal
            result.put("document", object);
            return result;
        }

        map.forEach((key, value) -> {
            if (value instanceof Map) {
                value = asMap(value);
            }
            if (key instanceof CharSequence) {
                result.put(key.toString(), value);
            } else {
                // It has to be a map key in this case
                result.put("[" + key.toString() + "]", value);
            }
        });
        return result;
    }

    protected final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        source.forEach((key, value) -> {
            if (StringUtils.hasText(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                } else {
                    key = path + '.' + key;
                }
            }
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof Map map) {
                // Need a compound key
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection collection) {
                // Need a compound key
                if (collection.isEmpty()) {
                    result.put(key, "");
                } else {
                    int count = 0;
                    for (Object object : collection) {
                        buildFlattenedMap(result, Collections.singletonMap(
                                "[" + (count++) + "]", object), key);
                    }
                }
            } else {
                result.put(key, (value != null ? value : ""));
            }
        });
    }

}
