package com.minis.core.type;

import com.minis.core.annotation.AnnotationAttributes;
import com.minis.core.annotation.MergedAnnotations;
import com.minis.core.annotation.TypeMappedAnnotations;
import com.minis.utils.ReflectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {
    private final MergedAnnotations mergedAnnotations;  // 注解信息

    private Set<String> annotationTypes; // all directly present annotation names

    public StandardAnnotationMetadata(Class<?> introspectedClass) {
        super(introspectedClass);
        this.mergedAnnotations = new TypeMappedAnnotations(introspectedClass);
    }

    public static AnnotationMetadata from(Class<?> introspectedClass) {
        return new StandardAnnotationMetadata(introspectedClass);
    }


    @Override
    public AnnotationAttributes getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return AnnotationMetadata.super.getAnnotationAttributes(annotationName, classValuesAsString);
    }

    @Override
    public MergedAnnotations getAnnotations() {
        return this.mergedAnnotations;
    }

    /**
     * @return all directly present annotation names
     */
    @Override
    public Set<String> getAnnotationTypes() {
        Set<String> annotationTypes = this.annotationTypes;
        if (annotationTypes == null) {
            annotationTypes = Collections.unmodifiableSet(AnnotationMetadata.super.getAnnotationTypes());
            this.annotationTypes = annotationTypes;
        }
        return annotationTypes;
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        Set<MethodMetadata> result = new LinkedHashSet<>(4);
        ReflectionUtils.doWithLocalMethods(getIntrospectedClass(), method -> {
            if (isAnnotatedMethod(method, annotationName)) {
                result.add(new StandardMethodMetadata(method));
            }
        });
        return Collections.unmodifiableSet(result);
    }

    private boolean isAnnotatedMethod(Method method, String annotationName) {
        return method.getAnnotations().length > 0 && isAnnotated(method, annotationName);
    }

    private static boolean isAnnotated(AnnotatedElement element, String annotationName) {
        return new TypeMappedAnnotations(element).isPresent(annotationName);
    }

}
