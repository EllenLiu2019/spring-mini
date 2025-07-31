package com.minis.core.annotation;


import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ToString
public class AnnotationTypeMappings {

    static final List<String> EXCLUDE_FILTER = List.of("java.lang", "org.springframework.lang");

    // TODO: 缓存 @SpringBootApplication 注解的元信息
    private static final Map<Class<? extends Annotation>, AnnotationTypeMappings> cacheAnnotationMappings = new ConcurrentHashMap<>();

    private final List<AnnotationTypeMapping> mappings;

    private AnnotationTypeMappings(Class<? extends Annotation> annotationType,
                                   Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        this.mappings = new ArrayList<>();
        addAllMappings(annotationType, visitedAnnotationTypes);
    }

    private void addAllMappings(Class<? extends Annotation> annotationType,
                                Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        Deque<AnnotationTypeMapping> queue = new ArrayDeque<>();
        addIfPossible(queue, null, annotationType, null, visitedAnnotationTypes);
        while (!queue.isEmpty()) {
            AnnotationTypeMapping mapping = queue.removeFirst();
            this.mappings.add(mapping);
            addMetaAnnotationsToQueue(queue, mapping);
        }
    }

    private void addIfPossible(Deque<AnnotationTypeMapping> queue, AnnotationTypeMapping source,
                               Class<? extends Annotation> annotationType, Annotation ann,
                               Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        try {
            queue.addLast(new AnnotationTypeMapping(source, annotationType, ann, visitedAnnotationTypes));
        } catch (Exception ex) {
            log.error("Failed to introspect meta-annotation " + annotationType.getName(),
                    (source != null ? source.getAnnotationType() : null), ex);
        }
    }

    private void addMetaAnnotationsToQueue(Deque<AnnotationTypeMapping> queue, AnnotationTypeMapping source) {
        Annotation[] metaAnnotations = source.getAnnotationType().getDeclaredAnnotations();
        for (Annotation metaAnnotation : metaAnnotations) {
            if (!isIgnorable(metaAnnotation.annotationType())) {
                if (isAlreadyMapped(source, metaAnnotation)) {
                    continue;
                }
                addIfPossible(queue, source, metaAnnotation);
            }
        }
    }

    private void addIfPossible(Deque<AnnotationTypeMapping> queue, AnnotationTypeMapping source, Annotation ann) {
        addIfPossible(queue, source, ann.annotationType(), ann, new HashSet<>());
    }

    static boolean isIgnorable(Class<? extends Annotation> aClass) {
        return EXCLUDE_FILTER.stream().anyMatch(exclude -> aClass.getName().startsWith(exclude));
    }

    private boolean isAlreadyMapped(AnnotationTypeMapping source, Annotation metaAnnotation) {
        Class<? extends Annotation> annotationType = metaAnnotation.annotationType();
        AnnotationTypeMapping mapping = source;
        while (mapping != null) {
            if (mapping.getAnnotationType() == annotationType) {
                return true;
            }
            mapping = mapping.getSource();
        }
        return false;
    }


    /**
     * @param annotationType
     * @return {@link AnnotationTypeMappings} 注解元信息
     */
    static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType) {
        return forAnnotationType(annotationType, new HashSet<>());
    }

    static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        return cacheAnnotationMappings.computeIfAbsent(annotationType, key -> createMappings(key, visitedAnnotationTypes));
    }

    private static AnnotationTypeMappings createMappings(Class<? extends Annotation> annotationType,
                                                         Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        return new AnnotationTypeMappings(annotationType, visitedAnnotationTypes);
    }

    int size() {
        return this.mappings.size();
    }

    AnnotationTypeMapping get(int index) {
        return this.mappings.get(index);
    }

    public List<AnnotationTypeMapping> getMappings() {
        return this.mappings;
    }

    public static Map<Class<? extends Annotation>, AnnotationTypeMappings> getCacheAnnotationMappings() {
        return cacheAnnotationMappings;
    }

}
