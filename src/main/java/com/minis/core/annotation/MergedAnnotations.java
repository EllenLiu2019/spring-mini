package com.minis.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface MergedAnnotations extends Iterable<MergedAnnotation<Annotation>> {

    <A extends Annotation> MergedAnnotation<A> get(String annotationType, Predicate<? super MergedAnnotation<A>> predicate);

    List<AnnotationTypeMappings> getAggregates();
    <A extends Annotation> MergedAnnotation<A> getAggregates(Class<? extends Annotation> annotationType);

    boolean isPresent(String annotationName);

    <A extends Annotation> List<MergedAnnotation<A>> getMergedAnnotations();
}
