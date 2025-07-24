package com.minis.core.annotation;


import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AnnotationTypeMapping {

    private final AnnotationTypeMapping source;

    private final AnnotationTypeMapping root;

    private final int distance; // Distance from root

    private final Class<? extends Annotation> annotationType;

    /**
     * The meta-annotations that are present on the annotation type.
     */
    private final List<Class<? extends Annotation>> metaTypes;

    private final Annotation annotation;

    private final AttributeMethods attributes;


    /**
     * Create a new AnnotationTypeMapping instance.
     *
     * @param source          the source mapping, or {@code null} if this is the root mapping
     * @param annotationType  the annotation type
     * @param annotation      the annotation instance
     * @param visitedAnnotationTypes the set of annotation types already visited
     */
    AnnotationTypeMapping(AnnotationTypeMapping source, Class<? extends Annotation> annotationType,
                          Annotation annotation, Set<Class<? extends Annotation>> visitedAnnotationTypes) {

        this.source = source;
        this.root = (source != null ? source.getRoot() : this);
        this.distance = (source == null ? 0 : source.getDistance() + 1);
        this.annotationType = annotationType;
        this.metaTypes = merge(source != null ? source.getMetaTypes() : null, annotationType);
        this.annotation = annotation;
        this.attributes = AttributeMethods.forAnnotationType(annotationType);
        visitedAnnotationTypes.add(annotationType);
    }

    /**
     * Merge the given list with the given element.
     */
    private static <T> List<T> merge(List<T> existing, T element) {
        if (existing == null) {
            return Collections.singletonList(element);
        }
        List<T> merged = new ArrayList<>(existing.size() + 1);
        merged.addAll(existing);
        merged.add(element);
        return Collections.unmodifiableList(merged);
    }

    AnnotationTypeMapping getRoot() {
        return this.root;
    }

    int getDistance() {
        return this.distance;
    }

    public Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }

    List<Class<? extends Annotation>> getMetaTypes() {
        return this.metaTypes;
    }

    AnnotationTypeMapping getSource() {
        return this.source;
    }

    /**
     * Return the attribute methods for this mapping.
     */
    public AttributeMethods getAttributes() {
        return this.attributes;
    }

    Annotation getAnnotation() {
        return this.annotation;
    }
}
