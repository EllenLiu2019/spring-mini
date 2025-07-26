package com.minis.core.type;

import com.minis.core.annotation.*;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface AnnotationMetadata extends ClassMetadata {

    // TODO: 反射，创建注解元数据
    static AnnotationMetadata introspect(Class<?> type) {
        return StandardAnnotationMetadata.from(type);
    }
    String getClassName();

    default AnnotationAttributes getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, false);
    }
    default AnnotationAttributes getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        MergedAnnotation<Annotation> annotation = getAnnotations().get(annotationName, null);
        if (annotation  != null) {
            if (!annotation.isPresent()) {
                return null;
            }
            return annotation.asAnnotationAttributes();
        }
        return null;
    }

    default Set<AnnotationAttributes> getAnnotationAttributes(Class<? extends Annotation> annotationType) {
        MergedAnnotation<Annotation> annotation = this.getAnnotations().getAggregates(annotationType);
        if (annotation != null && !annotation.isPresent()) {
            return null;
        }
        return annotation == null ? new LinkedHashSet<>() : Set.of(annotation.asAnnotationAttributes());
    }

    MergedAnnotations getAnnotations();

    default boolean isAnnotated(String name) {
        return getAnnotations().isPresent(name);
    }

    default Set<String> getAnnotationTypes() {
        return getAnnotations().getMergedAnnotations().stream()
                .filter(MergedAnnotation::isDirectlyPresent)
                .map(annotation -> annotation.getType().getName())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    boolean isInterface();
}
