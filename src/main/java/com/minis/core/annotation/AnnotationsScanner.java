package com.minis.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static com.minis.core.annotation.AnnotationTypeMappings.isIgnorable;

public class AnnotationsScanner {

    static boolean isKnownEmpty(AnnotatedElement source, MergedAnnotations.SearchStrategy searchStrategy) {
        if (hasPlainJavaAnnotationsOnly(source)) {
            return true;
        }
        if (searchStrategy == MergedAnnotations.SearchStrategy.DIRECT ) {
            if (source instanceof Method method && method.isBridge()) {
                return false;
            }
            return getDeclaredAnnotations(source, false).length == 0;
        }
        return false;
    }

    static boolean hasPlainJavaAnnotationsOnly(Object annotatedElement) {
        if (annotatedElement instanceof Class<?> clazz) {
            return hasPlainJavaAnnotationsOnly(clazz);
        } else if (annotatedElement instanceof Member member) {
            return hasPlainJavaAnnotationsOnly(member.getDeclaringClass());
        } else {
            return false;
        }
    }

    static boolean hasPlainJavaAnnotationsOnly(Class<?> type) {
        return (type.getName().startsWith("java."));
    }

    static Annotation[] getDeclaredAnnotations(AnnotatedElement source, boolean defensive) {
        boolean cached = false;
        Annotation[] annotations = source.getDeclaredAnnotations();
        if (annotations.length != 0) {
            boolean allIgnored = true;
            for (int i = 0; i < annotations.length; i++) {
                Annotation annotation = annotations[i];
                if (isIgnorable(annotation.annotationType()) ||
                        !AttributeMethods.forAnnotationType(annotation.annotationType()).canLoad(annotation)) {
                    annotations[i] = null;
                } else {
                    allIgnored = false;
                }
            }
            annotations = (allIgnored ? new Annotation[]{} : annotations);
            if (source instanceof Class || source instanceof Member) {
                cached = true;
            }
        }
        if (!defensive || annotations.length == 0 || !cached) {
            return annotations;
        }
        return annotations.clone();
    }

}
