package com.minis.beans.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

// A simple descriptor for an injection point, pointing to a method/constructor parameter or a field.
public class InjectionPoint {

    protected Field field;

    private volatile Annotation[] fieldAnnotations;

    public InjectionPoint(Field field) {
        this.field = field;
    }

    public Annotation[] getAnnotations() {
        Annotation[] fieldAnnotations = this.fieldAnnotations;
        if (fieldAnnotations == null) {
            fieldAnnotations = this.field.getAnnotations();
            this.fieldAnnotations = fieldAnnotations;
        }
        return fieldAnnotations;

    }
}
