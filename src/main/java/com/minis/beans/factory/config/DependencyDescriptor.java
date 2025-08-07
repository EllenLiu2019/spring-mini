package com.minis.beans.factory.config;

import com.minis.beans.factory.InjectionPoint;
import com.minis.core.ResolvableType;
import com.minis.core.convert.TypeDescriptor;

import java.lang.reflect.Field;

public class DependencyDescriptor extends InjectionPoint {

    private final Class<?> declaringClass;

    private String fieldName;

    private final boolean required = true;

    private Class<?> containingClass;

    private transient volatile TypeDescriptor typeDescriptor;

    private transient volatile ResolvableType resolvableType;

    public DependencyDescriptor(Field field) {
        super(field);
        this.declaringClass = field.getDeclaringClass();
        this.fieldName = field.getName();
    }

    public void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
    }

    public Class<?> getDependencyType() {
        return this.field.getType();
    }

    public TypeDescriptor getTypeDescriptor() {
        TypeDescriptor typeDescriptor = this.typeDescriptor;
        if (typeDescriptor == null) {
            typeDescriptor = new TypeDescriptor(getResolvableType(), getDependencyType());
            this.typeDescriptor = typeDescriptor;
        }
        return typeDescriptor;
    }

    public ResolvableType getResolvableType() {
        ResolvableType resolvableType = this.resolvableType;
        if (resolvableType == null) {
            resolvableType = ResolvableType.forField(this.field, this.containingClass);
            this.resolvableType = resolvableType;
        }
        return resolvableType;
    }
}
