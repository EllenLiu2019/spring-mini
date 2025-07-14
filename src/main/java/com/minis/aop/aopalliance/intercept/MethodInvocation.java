package com.minis.aop.aopalliance.intercept;

import java.lang.reflect.Method;

// TODO: 实现原有（未增强）方法调用
public interface MethodInvocation extends Invocation {
    Method getMethod();
}
