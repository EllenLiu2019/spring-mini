package com.minis.core.type;

import com.minis.app.App;
import com.minis.boot.autoconfigure.AutoConfigurationImportSelector;
import com.minis.context.annotation.Configuration;
import com.minis.context.annotation.Import;
import com.minis.core.annotation.AnnotationAttributes;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class AnnotationMetadataTest {

    AnnotationMetadata metadata = AnnotationMetadata.introspect(App.class);

    @Test
    void introspect() {
        assertNotNull(metadata.getAnnotations());
    }

    @Test
    void getAnnotationAttributes() {
        Map<String, Object> expectedAttributes = Map.of("proxyBeanMethods", true, "value", "");
        AnnotationAttributes annotationAttributes = metadata.getAnnotationAttributes(Configuration.class.getName());
        annotationAttributes.forEach((realKey, realValue) -> {
            assertEquals(expectedAttributes.get(realKey), realValue);
        });
        AnnotationAttributes config = metadata.getAnnotationAttributes(Import.class.getName());
        assertEquals(1, config.size());
        assertEquals(AutoConfigurationImportSelector.class, ((Class<?>[]) config.get("value"))[0]);
    }

    @Test
    void getAnnotationAttributesForClass() {
        Map<String, Object> expectedAttributes = Map.of("proxyBeanMethods", true, "value", "");
        Set<AnnotationAttributes> annotationAttributes = metadata.getAnnotationAttributes(Configuration.class);
        annotationAttributes.forEach(attributes -> {
            attributes.forEach((realKey, realValue) -> assertEquals(expectedAttributes.get(realKey), realValue));
        });

        Set<AnnotationAttributes> config = metadata.getAnnotationAttributes(Import.class);
        for (AnnotationAttributes attributes : config) {
            assertEquals(1, attributes.size());
            assertEquals(AutoConfigurationImportSelector.class, ((Class<?>[]) attributes.get("value"))[0]);
        }
    }
}