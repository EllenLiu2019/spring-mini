package com.minis.aop;

// TODO: 方法拦截器, use input parameter MethodInvocation to proceed:
//  both enhanced-method & original-method
public interface MethodInterceptor extends Interceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
