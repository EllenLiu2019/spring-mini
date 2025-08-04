package com.minis.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public abstract class AnnotatedElementUtils {

    public static AnnotationAttributes getMergedAnnotationAttributes(
            AnnotatedElement element, Class<? extends Annotation> annotationType) {

        MergedAnnotation<?> mergedAnnotation = getAnnotations(element).get(annotationType);
        return mergedAnnotation.asAnnotationAttributes();
    }

    private static MergedAnnotations getAnnotations(AnnotatedElement element) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS);
    }

    public static AnnotatedElement forAnnotations(Annotation[] annotations) {
        return new AnnotatedElementForAnnotations(annotations);
    }

    public static class AnnotatedElementForAnnotations implements AnnotatedElement {

        private final Annotation[] annotations;

        AnnotatedElementForAnnotations(Annotation... annotations) {
            this.annotations = annotations;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            for (Annotation annotation : this.annotations) {
                if (annotation.annotationType() == annotationClass) {
                    return (T) annotation;
                }
            }
            return null;
        }

        @Override
        public Annotation[] getAnnotations() {
            return this.annotations.clone();
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return this.annotations.clone();
        }
    }
}
