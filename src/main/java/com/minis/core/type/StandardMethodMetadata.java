package com.minis.core.type;

import com.minis.core.annotation.MergedAnnotations;
import com.minis.core.annotation.TypeMappedAnnotations;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

public class StandardMethodMetadata implements MethodMetadata {

    private final Method introspectedMethod;

    private final MergedAnnotations mergedAnnotations;

    StandardMethodMetadata(Method introspectedMethod) {
        this.introspectedMethod = introspectedMethod;
        this.mergedAnnotations = new TypeMappedAnnotations(introspectedMethod);
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("not a class");
    }

    @Override
    public MergedAnnotations getAnnotations() {
        return this.mergedAnnotations;
    }

    @Override
    public boolean isInterface() {
        throw new UnsupportedOperationException("not a class");
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isIndependent() {
        throw new UnsupportedOperationException("not a class");
    }

    @Override
    public String getEnclosingClassName() {
        throw new UnsupportedOperationException("not a class");
    }

    @Override
    public String getSuperClassName() {
        throw new UnsupportedOperationException("not a class");
    }

    @Override
    public String[] getInterfaceNames() {
        throw new UnsupportedOperationException("not a class");
    }

    @Override
    public String[] getMemberClassNames() {
        throw new UnsupportedOperationException("not a class");
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        throw new UnsupportedOperationException("not a class");
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.introspectedMethod.getModifiers());
    }

    @Override
    public String getMethodName() {
        return this.introspectedMethod.getName();
    }

    @Override
    public String getReturnTypeName() {
        return this.introspectedMethod.getReturnType().getName();
    }

}
