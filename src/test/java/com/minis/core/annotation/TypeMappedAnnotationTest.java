package com.minis.core.annotation;

import com.minis.app.App;
import com.minis.boot.autoconfigure.AutoConfigurationImportSelector;
import com.minis.context.annotation.Import;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.*;

class TypeMappedAnnotationTest {

    @Test
    void test_asAnnotationAttributes() {
        TypeMappedAnnotations typeMappedAnnotations = new TypeMappedAnnotations(App.class);
        MergedAnnotation<Annotation> aggregates = typeMappedAnnotations.getAggregates(Import.class);
        AnnotationAttributes attributes = aggregates.asAnnotationAttributes();
        assertEquals(1, attributes.size());
        assertEquals(AutoConfigurationImportSelector.class, ((Class<?>[])attributes.get("value"))[0]);
    }

}