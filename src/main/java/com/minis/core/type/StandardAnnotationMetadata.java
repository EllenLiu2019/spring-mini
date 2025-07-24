package com.minis.core.type;

import com.minis.core.annotation.AnnotationAttributes;
import com.minis.core.annotation.MergedAnnotations;
import com.minis.core.annotation.TypeMappedAnnotations;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {
    private final MergedAnnotations mergedAnnotations;  // 注解信息

    private Set<String> annotationTypes; // all directly present annotation names

    public StandardAnnotationMetadata(Class<?> introspectedClass) {
        super(introspectedClass);
        this.mergedAnnotations = new TypeMappedAnnotations(introspectedClass);
    }

    public static AnnotationMetadata from(Class<?> introspectedClass) {
        return new StandardAnnotationMetadata(introspectedClass);
    }


    @Override
    public AnnotationAttributes getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return AnnotationMetadata.super.getAnnotationAttributes(annotationName, classValuesAsString);
    }

    @Override
    public MergedAnnotations getAnnotations() {
        return this.mergedAnnotations;
    }

    /**
     * @return all directly present annotation names
     */
    @Override
    public Set<String> getAnnotationTypes() {
        Set<String> annotationTypes = this.annotationTypes;
        if (annotationTypes == null) {
            annotationTypes = Collections.unmodifiableSet(AnnotationMetadata.super.getAnnotationTypes());
            this.annotationTypes = annotationTypes;
        }
        return annotationTypes;
    }

}
