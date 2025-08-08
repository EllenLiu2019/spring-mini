package com.minis.core.annotation;

import com.minis.core.MethodParameter;

import java.lang.reflect.Method;

public class AnnotatedMethod {

    private Method method;

    private MethodParameter[] parameters;

    public AnnotatedMethod(Method method) {
        this.method = method;
        this.parameters = initMethodParameters();
    }

    public AnnotatedMethod(AnnotatedMethod annotatedMethod) {
        this.method = annotatedMethod.method;
        this.parameters = annotatedMethod.parameters;
    }

    private MethodParameter[] initMethodParameters() {
        int count = this.method.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            result[i] = new MethodParameter(this.method, i);
        }
        return result;
    }

    public final MethodParameter[] getMethodParameters() {
        return this.parameters;
    }

    public Method getMethod() {
        return this.method;
    }
}
