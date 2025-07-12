package com.minis.aop;

import java.lang.reflect.Method;

// TODO: 实现原有（未增强）方法调用
public interface MethodInvocation {
    Method getMethod();
    Object[] getArguments();
    Object getThis();
    Object proceed() throws Throwable; // TODO: use reflection mechanism to invoke the real target method
}
