package com.minis.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnnotationAttributes extends LinkedHashMap<String, Object> {

    private static final String UNKNOWN = "unknown";

    private final Class<? extends Annotation> annotationType;

    final String displayName;

    final boolean validated;

    public AnnotationAttributes() {
        this.annotationType = null;
        this.displayName = UNKNOWN;
        this.validated = false;
    }

    public AnnotationAttributes(Map<String, Object> map) {
        super(map);
        this.annotationType = null;
        this.displayName = UNKNOWN;
        this.validated = false;
    }

    public AnnotationAttributes(Class<? extends Annotation> annotationType) {
        this(annotationType, false);
    }

    AnnotationAttributes(Class<? extends Annotation> annotationType, boolean validated) {
        this.annotationType = annotationType;
        this.displayName = annotationType.getName();
        this.validated = validated;
    }

    public Class<? extends Annotation> annotationType() {
        return this.annotationType;
    }

    public String getString(String attributeName) {
        return getRequiredAttribute(attributeName, String.class);
    }

    public String[] getStringArray(String attributeName) {
        return getRequiredAttribute(attributeName, String[].class);
    }

    @SuppressWarnings("unchecked")
    private <T> T getRequiredAttribute(String attributeName, Class<T> expectedType) {
        Object value = get(attributeName);
        if (value == null) {
            throw new IllegalArgumentException(String.format(
                    "Attribute '%s' not found in attributes for annotation [%s]",
                    attributeName, this.displayName));
        }
        if (value instanceof Throwable throwable) {
            throw new IllegalArgumentException(String.format(
                    "Attribute '%s' for annotation [%s] was not resolvable due to exception [%s]",
                    attributeName, this.displayName, value), throwable);
        }
        if (!expectedType.isInstance(value) && expectedType.isArray() &&
                expectedType.componentType().isInstance(value)) {
            Object array = Array.newInstance(expectedType.componentType(), 1);
            Array.set(array, 0, value);
            value = array;
        }
        if (!expectedType.isInstance(value)) {
            throw new IllegalArgumentException(String.format(
                    "Attribute '%s' is of type %s, but %s was expected in attributes for annotation [%s]",
                    attributeName, value.getClass().getSimpleName(), expectedType.getSimpleName(),
                    this.displayName));
        }
        return (T) value;
    }

    public Class<?>[] getClassArray(String attributeName) {
        return getRequiredAttribute(attributeName, Class[].class);
    }

    public static AnnotationAttributes fromMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        if (map instanceof AnnotationAttributes annotationAttributes) {
            return annotationAttributes;
        }
        return new AnnotationAttributes(map);
    }
}
