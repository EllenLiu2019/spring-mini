package com.minis.web.servlet;


import com.minis.core.annotation.AnnotatedMethod;

import java.lang.reflect.Method;

public class HandlerMethod extends AnnotatedMethod {
    private Object bean;
    private Method method;

    public HandlerMethod(Object bean, Method method) {
        super(method);
        this.bean = bean;
        this.method = method;
    }

    public HandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
        this.bean = handlerMethod.getBean();
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return super.getMethod();
    }
}
