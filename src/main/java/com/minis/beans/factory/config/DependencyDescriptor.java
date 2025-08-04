package com.minis.beans.factory.config;

import com.minis.beans.factory.InjectionPoint;

import java.lang.reflect.Field;

public class DependencyDescriptor extends InjectionPoint {

    private final Class<?> declaringClass;

    private String fieldName;

    private final boolean required;

    private final boolean eager;

    private Class<?> containingClass;

    public DependencyDescriptor(Field field) {
        this(field, true, true);
    }

    public DependencyDescriptor(Field field, boolean required, boolean eager) {
        super(field);
        this.declaringClass = field.getDeclaringClass();
        this.fieldName = field.getName();
        this.required = required;
        this.eager = eager;
    }

    public void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
    }

    public Class<?> getDependencyType() {
        return this.field.getType();
    }
}
