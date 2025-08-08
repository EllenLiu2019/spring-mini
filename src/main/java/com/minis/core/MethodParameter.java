package com.minis.core;

import com.minis.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

public class MethodParameter {

    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

    private final Executable executable;

    private final int parameterIndex;

    private volatile Parameter parameter;

    private volatile Class<?> containingClass;

    private volatile Class<?> parameterType;

    private volatile Type genericParameterType;

    private volatile Annotation[] parameterAnnotations;

    volatile String parameterName;

    public MethodParameter(Method method, int parameterIndex) {
        this.executable = method;
        this.parameterIndex = parameterIndex;
    }

    public MethodParameter(Constructor<?> constructor, int parameterIndex) {
        this.executable = constructor;
        this.parameterIndex = parameterIndex;
    }

    public Executable getExecutable() {
        return this.executable;
    }

    public Method getMethod() {
        return (this.executable instanceof Method method ? method : null);
    }

    public int getParameterIndex() {
        return this.parameterIndex;
    }

    public Parameter getParameter() {
        if (this.parameterIndex < 0) {
            throw new IllegalStateException("Cannot retrieve Parameter descriptor for method return type");
        }
        Parameter parameter = this.parameter;
        if (parameter == null) {
            parameter = getExecutable().getParameters()[this.parameterIndex];
            this.parameter = parameter;
        }
        return parameter;
    }

    public Class<?> getContainingClass() {
        Class<?> containingClass = this.containingClass;
        return (containingClass != null ? containingClass : getDeclaringClass());
    }

    public Class<?> getDeclaringClass() {
        return this.executable.getDeclaringClass();
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        if (this.parameterIndex < 0) {
            return null;
        }
        if (this.parameterName != null) {
            return this.parameterName;
        }
        this.parameterName = getParameter().getName();
        return this.parameterName;
    }

    public Class<?> getParameterType() {
        Class<?> paramType = this.parameterType;
        if (paramType != null) {
            return paramType;
        }
        if (getContainingClass() != getDeclaringClass()) {
            paramType = ResolvableType.forMethodParameter(this, null).resolve();
        }
        if (paramType == null) {
            paramType = computeParameterType();
        }
        this.parameterType = paramType;
        return paramType;
    }

    public Type getGenericParameterType() {
        Type paramType = this.genericParameterType;
        if (paramType == null) {
            Type[] genericParameterTypes = this.executable.getGenericParameterTypes();
            int index = this.parameterIndex;
            if (this.executable instanceof Constructor &&
                    ClassUtils.isInnerClass(this.executable.getDeclaringClass()) &&
                    genericParameterTypes.length == this.executable.getParameterCount() - 1) {
                // Bug in javac: type array excludes enclosing instance parameter
                // for inner classes with at least one generic constructor parameter,
                // so access it with the actual parameter index lowered by 1
                index = this.parameterIndex - 1;
            }
            paramType = (index >= 0 && index < genericParameterTypes.length ?
                    genericParameterTypes[index] : computeParameterType());
            this.genericParameterType = paramType;
        }
        return paramType;
    }

    private Class<?> computeParameterType() {
        return this.executable.getParameterTypes()[this.parameterIndex];
    }


}
