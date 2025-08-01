package com.minis.core.type.classreading;

import com.minis.core.annotation.MergedAnnotations;
import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.MethodMetadata;
import com.minis.utils.StringUtils;
import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

final class SimpleAnnotationMetadata implements AnnotationMetadata {

    private final String className;

    private final int access;

    private final String enclosingClassName;

    private final String superClassName;

    private final boolean independentInnerClass;

    private final Set<String> interfaceNames;

    private final Set<String> memberClassNames;

    private final Set<MethodMetadata> declaredMethods;

    private final MergedAnnotations mergedAnnotations;

    private Set<String> annotationTypes;

    SimpleAnnotationMetadata(String className, int access, String enclosingClassName,
                             String superClassName, boolean independentInnerClass, Set<String> interfaceNames,
                             Set<String> memberClassNames, Set<MethodMetadata> declaredMethods, MergedAnnotations mergedAnnotations) {

        this.className = className;
        this.access = access;
        this.enclosingClassName = enclosingClassName;
        this.superClassName = superClassName;
        this.independentInnerClass = independentInnerClass;
        this.interfaceNames = interfaceNames;
        this.memberClassNames = memberClassNames;
        this.declaredMethods = declaredMethods;
        this.mergedAnnotations = mergedAnnotations;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public MergedAnnotations getAnnotations() {
        return this.mergedAnnotations;
    }

    @Override
    public Set<String> getAnnotationTypes() {
        Set<String> annotationTypes = this.annotationTypes;
        if (annotationTypes == null) {
            annotationTypes = Collections.unmodifiableSet(
                    AnnotationMetadata.super.getAnnotationTypes());
            this.annotationTypes = annotationTypes;
        }
        return annotationTypes;
    }

    @Override
    public boolean isInterface() {
        return (this.access & Opcodes.ACC_INTERFACE) != 0;
    }

    @Override
    public boolean isAbstract() {
        return (this.access & Opcodes.ACC_ABSTRACT) != 0;
    }

    @Override
    public boolean isFinal() {
        return (this.access & Opcodes.ACC_FINAL) != 0;
    }

    @Override
    public boolean isIndependent() {
        return (this.enclosingClassName == null || this.independentInnerClass);
    }

    @Override
    public String getEnclosingClassName() {
        return this.enclosingClassName;
    }

    @Override
    public String getSuperClassName() {
        return this.superClassName;
    }

    @Override
    public String[] getInterfaceNames() {
        return StringUtils.toStringArray(this.interfaceNames);
    }

    @Override
    public String[] getMemberClassNames() {
        return StringUtils.toStringArray(this.memberClassNames);
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        Set<MethodMetadata> result = new LinkedHashSet<>(4);
        for (MethodMetadata annotatedMethod : this.declaredMethods) {
            if (annotatedMethod.isAnnotated(annotationName)) {
                result.add(annotatedMethod);
            }
        }
        return Collections.unmodifiableSet(result);
    }
}
