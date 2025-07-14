package com.minis.aop.aopalliance.intercept;

// TODO: 方法拦截器, use input parameter MethodInvocation to proceed:
//  both enhanced-method & original-method

/**
 * TODO：是函数式接口；用于拦截 【MethodInvocation】 的连接点
 */
public interface MethodInterceptor extends Interceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
