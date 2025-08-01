package com.minis.core.annotation;


import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.function.Predicate;

import static com.minis.core.annotation.AnnotationTypeMappings.isIgnorable;

public class TypeMappedAnnotations implements MergedAnnotations {

    static final MergedAnnotations NONE = new TypeMappedAnnotations(null, new Annotation[0]);

    private final Object source; // 类 & 方法

    private final AnnotatedElement element; // 类 & 方法

    private List<AnnotationTypeMappings> aggregates;

    private final Annotation[] annotations;


    public TypeMappedAnnotations(AnnotatedElement element) {
        this.source = element;
        this.element = element;
        this.annotations = null;
    }

    private TypeMappedAnnotations(Object source, Annotation[] annotations) {
        this.source = source;
        this.element = null;
        this.annotations = annotations;
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(String annotationType, Predicate<? super MergedAnnotation<A>> predicate) {
        Set<Annotation> annotations = this.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            AnnotationTypeMappings mappings = AnnotationTypeMappings.forAnnotationType(annotation.annotationType());
            for (int i = 0; i < mappings.size(); i++) {
                AnnotationTypeMapping mapping = mappings.get(i);
                if (isMappingForType(mapping, annotationType)) {
                    return new TypeMappedAnnotation<>(mapping, source, annotation);
                }
            }
        }
        return null;
    }

    public List<AnnotationTypeMappings> getAggregates() {
        List<AnnotationTypeMappings> aggregates = this.aggregates;
        if (aggregates == null) {
            aggregates = new ArrayList<>();
            Set<Annotation> annotations = this.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                aggregates.add(AnnotationTypeMappings.forAnnotationType(annotation.annotationType()));
            }
            if (aggregates.isEmpty()) {
                aggregates = Collections.emptyList();
            }
            this.aggregates = aggregates;
        }
        return aggregates;
    }

    public <A extends Annotation> List<MergedAnnotation<A>> getMergedAnnotations() {
        final List<MergedAnnotation<A>> result = new ArrayList<>();
        List<AnnotationTypeMappings> aggregates = this.getAggregates();
        for (AnnotationTypeMappings annotations : aggregates) {
            List<AnnotationTypeMapping> mappings = annotations.getMappings();
            mappings.forEach(mapping -> result.add(new TypeMappedAnnotation<>(mapping, source, null)));
        }
        return result;
    }

    public <A extends Annotation> MergedAnnotation<A> getAggregates(Class<? extends Annotation> annotationType) {
        List<AnnotationTypeMappings> mappings = this.getAggregates();
        for (AnnotationTypeMappings mapping : mappings) {
            for (int i = 0; i < mapping.size(); i++) {
                AnnotationTypeMapping typeMapping = mapping.get(i);
                if (isMappingForType(typeMapping, annotationType)) {
                    return new TypeMappedAnnotation<>(typeMapping, source, null);
                }
            }
        }
        return null;
    }

    @Override
    public boolean isPresent(String annotationName) {
        List<AnnotationTypeMappings> mappings = this.getAggregates();
        for (AnnotationTypeMappings mapping : mappings) {
            for (int i = 0; i < mapping.size(); i++) {
                AnnotationTypeMapping typeMapping = mapping.get(i);
                if (isMappingForType(typeMapping, annotationName)) {
                    return true;
                }
            }
        }
        return false;
    }

    // App.class -> @SpringBootApplication
    // greeting -> @Bean
    private Set<Annotation> getDeclaredAnnotations() {
        if (this.element == null) return Collections.emptySet();
        Set<Annotation> result = new LinkedHashSet<>();
        Annotation[] annotations = this.element.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (!isIgnorable(annotation.annotationType())) {
                result.add(annotation);
            }
        }
        return result;
    }

    private static boolean isMappingForType(AnnotationTypeMapping mapping, Object requiredType) {
        Class<? extends Annotation> actualType = mapping.getAnnotationType();
        return (requiredType == null || actualType == requiredType || actualType.getName().equals(requiredType));
    }

    @Override
    public Iterator<MergedAnnotation<Annotation>> iterator() {
        return this.getMergedAnnotations().iterator();
    }
}
