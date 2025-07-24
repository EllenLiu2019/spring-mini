package com.minis.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.function.Function;

public interface MergedAnnotation<A extends Annotation> {
    String VALUE = "value";

    Class<A> getType();

    boolean isPresent();

    /**
     * Get the source that ultimately declared the root annotation, or
     * {@code null} if the source is not known.
     * If this merged annotation was created from AnnotatedElement
     * then this source will be an element of the same type.
     * If the annotation was loaded without using reflection, the source
     * can be of any type, but should have a sensible {@code toString()}.
     * Meta-annotations will always return the same source as the #getRoot().
     * @return the source, or null
     */
    Object getSource();

    MergedAnnotation<?> getMetaSource();
    AnnotationAttributes asAnnotationAttributes();

    <T extends Map<String, Object>> T asMap(Function<MergedAnnotation<?>, T> factory);

    boolean isDirectlyPresent();

    int getDistance();

    boolean isMetaPresent();
}
