package com.minis.boot.env;

import com.minis.core.env.PropertySource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class YamlPropertySourceLoaderTest {

    YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Test
    @SuppressWarnings("unchecked")
    void load() throws IOException {
        Map<String, Object> map = Map.of("server.port", 8088,
                "spring.application.name", "spring-mini",
                "spring.main.allow-circular-references", true,
                "datasource.spring-mini.jdbc-url", "jdbc:test://mock:1000/spring-mini",
                "datasource.spring-mini.username", "username",
                "datasource.spring-mini.password", "password");
        List<PropertySource<?>> propertySources = loader.load();
        assertEquals(1, propertySources.size());
        ((Map<String, Object>) propertySources.get(0).getSource())
                .forEach((key, value) -> assertEquals(map.get(key), value));
    }
}