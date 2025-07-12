package com.minis.aop;

import java.lang.reflect.Method;

public class ReflectiveMethodInvocation implements MethodInvocation {
    protected final Object proxy;
    protected final Object target;
    protected final Method method;
    protected final Object[] arguments;

    public ReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    // TODO: the real target method invoking
    public Object proceed() throws Throwable {
        return this.method.invoke(target, arguments);
    }
}
