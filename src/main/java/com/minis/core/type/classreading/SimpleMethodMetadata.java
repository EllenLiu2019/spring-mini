package com.minis.core.type.classreading;

import com.minis.core.annotation.MergedAnnotations;
import com.minis.core.type.MethodMetadata;
import org.objectweb.asm.Opcodes;

import java.util.Set;

final class SimpleMethodMetadata implements MethodMetadata {

    private final String methodName;

    private final int access;

    private final String declaringClassName;

    private final String returnTypeName;

    // The source implements equals(), hashCode(), and toString() for the underlying method.
    private final Object source;

    private final MergedAnnotations annotations;

    SimpleMethodMetadata(String methodName, int access, String declaringClassName,
                         String returnTypeName, Object source, MergedAnnotations annotations) {
        this.methodName = methodName;
        this.access = access;
        this.declaringClassName = declaringClassName;
        this.returnTypeName = returnTypeName;
        this.source = source;
        this.annotations = annotations;
    }

    @Override
    public String getMethodName() {
        return this.methodName;
    }

    public String getDeclaringClassName() {
        return this.declaringClassName;
    }

    @Override
    public String getReturnTypeName() {
        return this.returnTypeName;
    }

    @Override
    public boolean isAbstract() {
        return (this.access & Opcodes.ACC_ABSTRACT) != 0;
    }

    @Override
    public boolean isStatic() {
        return (this.access & Opcodes.ACC_STATIC) != 0;
    }

    @Override
    public boolean isFinal() {
        return (this.access & Opcodes.ACC_FINAL) != 0;
    }

    @Override
    public boolean isIndependent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEnclosingClassName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSuperClassName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getInterfaceNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getMemberClassNames() {
        throw new UnsupportedOperationException();
    }

    public boolean isOverridable() {
        return !isStatic() && !isFinal() && !isPrivate();
    }

    private boolean isPrivate() {
        return (this.access & Opcodes.ACC_PRIVATE) != 0;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MergedAnnotations getAnnotations() {
        return this.annotations;
    }

    @Override
    public boolean isInterface() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof SimpleMethodMetadata that && this.source.equals(that.source)));
    }

    @Override
    public int hashCode() {
        return this.source.hashCode();
    }

    @Override
    public String toString() {
        return this.source.toString();
    }
}
