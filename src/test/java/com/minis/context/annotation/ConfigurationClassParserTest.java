package com.minis.context.annotation;

import com.minis.core.annotation.MergedAnnotations;
import com.minis.core.type.AnnotationMetadata;
import org.junit.jupiter.api.Test;

import java.util.Set;


class ConfigurationClassParserTest {

    @Test
    void test_asSourceClass() {
        String className = "com.minis.context.annotation.Configuration";
        try {
            Class<?> aClass = Class.forName(className);
            AnnotationMetadata metadata = AnnotationMetadata.introspect(aClass);
            MergedAnnotations annotations = metadata.getAnnotations();
            Set<String> annotationTypes = metadata.getAnnotationTypes();
            System.out.println(annotationTypes);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}