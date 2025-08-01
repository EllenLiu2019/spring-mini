package com.minis.core.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

final class MergedAnnotationsCollection implements MergedAnnotations {

    private final MergedAnnotation<?>[] annotations;

    private final AnnotationTypeMappings[] mappings;

    private MergedAnnotationsCollection(Collection<MergedAnnotation<?>> annotations) {
        this.annotations = annotations.toArray(new MergedAnnotation<?>[0]);
        this.mappings = new AnnotationTypeMappings[this.annotations.length];
        for (int i = 0; i < this.annotations.length; i++) {
            MergedAnnotation<?> annotation = this.annotations[i];
            this.mappings[i] = AnnotationTypeMappings.forAnnotationType(annotation.getType());
        }
    }

    static MergedAnnotations of(Collection<MergedAnnotation<?>> annotations) {
        if (annotations.isEmpty()) {
            return TypeMappedAnnotations.NONE;
        }
        return new MergedAnnotationsCollection(annotations);
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(String requiredType, Predicate<? super MergedAnnotation<A>> predicate) {
        MergedAnnotation<A> result = null;
        for (int i = 0; i < this.annotations.length; i++) {
            MergedAnnotation<?> root = this.annotations[i];
            if (root != null) {
                AnnotationTypeMappings mappings = this.mappings[i];
                for (int mappingIndex = 0; mappingIndex < mappings.size(); mappingIndex++) {
                    AnnotationTypeMapping mapping = mappings.get(mappingIndex);
                    if (!isMappingForType(mapping, requiredType)) {
                        continue;
                    }
                    result = (mappingIndex == 0 ? (MergedAnnotation<A>) root : new TypeMappedAnnotation<>(mapping, root, null));
                }
            }
        }
        return result;
    }

    @Override
    public List<AnnotationTypeMappings> getAggregates() {
        return null;
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> getAggregates(Class<? extends Annotation> annotationType) {
        return null;
    }

    @Override
    public boolean isPresent(String annotationType) {
        return isPresent(annotationType, false);
    }

    private boolean isPresent(Object requiredType, boolean directOnly) {
        for (MergedAnnotation<?> annotation : this.annotations) {
            Class<? extends Annotation> type = annotation.getType();
            if (type == requiredType || type.getName().equals(requiredType)) {
                return true;
            }
        }
        if (!directOnly) {
            for (AnnotationTypeMappings mappings : this.mappings) {
                for (int i = 1; i < mappings.size(); i++) {
                    AnnotationTypeMapping mapping = mappings.get(i);
                    if (isMappingForType(mapping, requiredType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isMappingForType(AnnotationTypeMapping mapping, Object requiredType) {
        if (requiredType == null) {
            return true;
        }
        Class<? extends Annotation> actualType = mapping.getAnnotationType();
        return (actualType == requiredType || actualType.getName().equals(requiredType));
    }

    @Override
    public <A extends Annotation> List<MergedAnnotation<A>> getMergedAnnotations() {
        final List<MergedAnnotation<A>> result = new ArrayList<>();
        for (AnnotationTypeMappings annotations : this.mappings) {
            List<AnnotationTypeMapping> mappings = annotations.getMappings();
            mappings.forEach(mapping -> result.add(new TypeMappedAnnotation<>(mapping, null, null)));
        }
        return result;
    }

    @Override
    public Iterator<MergedAnnotation<Annotation>> iterator() {
        return null;
    }

}
