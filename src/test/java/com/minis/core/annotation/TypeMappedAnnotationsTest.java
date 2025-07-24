package com.minis.core.annotation;

import com.minis.app.App;
import com.minis.boot.SpringBootConfiguration;
import com.minis.boot.autoconfigure.AutoConfigurationPackage;
import com.minis.boot.autoconfigure.EnableAutoConfiguration;
import com.minis.boot.autoconfigure.SpringBootApplication;
import com.minis.context.annotation.ComponentScan;
import com.minis.context.annotation.Configuration;
import com.minis.context.annotation.Import;
import com.minis.stereotype.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TypeMappedAnnotationsTest {

    TypeMappedAnnotations typeMappedAnnotations = new TypeMappedAnnotations(App.class);

    @Test
    void test_getAggregates() {
        AnnotationTypeMappings annotationTypeMappings = typeMappedAnnotations.getAggregates().get(0);
        List<AnnotationTypeMapping> mappings = annotationTypeMappings.getMappings();

        AnnotationTypeMapping annotationTypeMapping_0 = mappings.get(0);
        assertNull(annotationTypeMapping_0.getSource());
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_0.getRoot());
        assertEquals(0, annotationTypeMapping_0.getDistance());
        assertEquals(SpringBootApplication.class, annotationTypeMapping_0.getAnnotationType());
        assertEquals(1, annotationTypeMapping_0.getMetaTypes().size());
        assertNull(annotationTypeMapping_0.getAnnotation());
        assertEquals(AttributeMethods.cache.get(SpringBootApplication.class), annotationTypeMapping_0.getAttributes());

        AnnotationTypeMapping annotationTypeMapping_1 = mappings.get(1);
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_1.getSource());
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_1.getRoot());
        assertEquals(1, annotationTypeMapping_1.getDistance());
        assertEquals(SpringBootConfiguration.class, annotationTypeMapping_1.getAnnotationType());
        assertEquals(2, annotationTypeMapping_1.getMetaTypes().size());
        assertEquals(SpringBootConfiguration.class, annotationTypeMapping_1.getAnnotation().annotationType());
        assertEquals(AttributeMethods.cache.get(SpringBootConfiguration.class), annotationTypeMapping_1.getAttributes());

        AnnotationTypeMapping annotationTypeMapping_2 = mappings.get(2);
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_2.getSource());
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_2.getRoot());
        assertEquals(1, annotationTypeMapping_2.getDistance());
        assertEquals(EnableAutoConfiguration.class, annotationTypeMapping_2.getAnnotationType());
        assertEquals(2, annotationTypeMapping_1.getMetaTypes().size());
        assertEquals(EnableAutoConfiguration.class, annotationTypeMapping_2.getAnnotation().annotationType());
        assertEquals(AttributeMethods.cache.get(EnableAutoConfiguration.class), annotationTypeMapping_2.getAttributes());

        AnnotationTypeMapping annotationTypeMapping_3 = mappings.get(3);
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_3.getSource());
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_3.getRoot());
        assertEquals(1, annotationTypeMapping_3.getDistance());
        assertEquals(ComponentScan.class, annotationTypeMapping_3.getAnnotationType());
        assertEquals(2, annotationTypeMapping_3.getMetaTypes().size());
        assertEquals(ComponentScan.class, annotationTypeMapping_3.getAnnotation().annotationType());
        assertEquals(AttributeMethods.cache.get(ComponentScan.class), annotationTypeMapping_3.getAttributes());

        AnnotationTypeMapping annotationTypeMapping_4 = mappings.get(4);
        assertEquals(annotationTypeMapping_1, annotationTypeMapping_4.getSource());
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_4.getRoot());
        assertEquals(2, annotationTypeMapping_4.getDistance());
        assertEquals(Configuration.class, annotationTypeMapping_4.getAnnotationType());
        assertEquals(3, annotationTypeMapping_4.getMetaTypes().size());
        assertEquals(Configuration.class, annotationTypeMapping_4.getAnnotation().annotationType());
        assertEquals(AttributeMethods.cache.get(Configuration.class), annotationTypeMapping_4.getAttributes());

        AnnotationTypeMapping annotationTypeMapping_5 = mappings.get(5);
        assertEquals(annotationTypeMapping_2, annotationTypeMapping_5.getSource());
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_5.getRoot());
        assertEquals(2, annotationTypeMapping_5.getDistance());
        assertEquals(AutoConfigurationPackage.class, annotationTypeMapping_5.getAnnotationType());
        assertEquals(3, annotationTypeMapping_5.getMetaTypes().size());
        assertEquals(AutoConfigurationPackage.class, annotationTypeMapping_5.getAnnotation().annotationType());
        assertEquals(AttributeMethods.cache.get(AutoConfigurationPackage.class), annotationTypeMapping_5.getAttributes());

        AnnotationTypeMapping annotationTypeMapping_6 = mappings.get(6);
        assertEquals(annotationTypeMapping_2, annotationTypeMapping_6.getSource());
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_6.getRoot());
        assertEquals(2, annotationTypeMapping_6.getDistance());
        assertEquals(Import.class, annotationTypeMapping_6.getAnnotationType());
        assertEquals(3, annotationTypeMapping_6.getMetaTypes().size());
        assertEquals(Import.class, annotationTypeMapping_6.getAnnotation().annotationType());
        assertEquals(AttributeMethods.cache.get(Import.class), annotationTypeMapping_6.getAttributes());

        AnnotationTypeMapping annotationTypeMapping_7 = mappings.get(7);
        assertEquals(annotationTypeMapping_4, annotationTypeMapping_7.getSource());
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_7.getRoot());
        assertEquals(3, annotationTypeMapping_7.getDistance());
        assertEquals(Component.class, annotationTypeMapping_7.getAnnotationType());
        assertEquals(4, annotationTypeMapping_7.getMetaTypes().size());
        assertEquals(Component.class, annotationTypeMapping_7.getAnnotation().annotationType());
        assertEquals(AttributeMethods.cache.get(Component.class), annotationTypeMapping_7.getAttributes());

        AnnotationTypeMapping annotationTypeMapping_8 = mappings.get(8);
        assertEquals(annotationTypeMapping_5, annotationTypeMapping_8.getSource());
        assertEquals(annotationTypeMapping_0, annotationTypeMapping_8.getRoot());
        assertEquals(3, annotationTypeMapping_8.getDistance());
        assertEquals(Import.class, annotationTypeMapping_8.getAnnotationType());
        assertEquals(4, annotationTypeMapping_8.getMetaTypes().size());
        assertEquals(Import.class, annotationTypeMapping_8.getAnnotation().annotationType());
        assertEquals(AttributeMethods.cache.get(Import.class), annotationTypeMapping_8.getAttributes());

    }
}