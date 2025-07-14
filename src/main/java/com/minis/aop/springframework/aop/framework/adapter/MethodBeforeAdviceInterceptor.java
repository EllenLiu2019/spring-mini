package com.minis.aop.springframework.aop.framework.adapter;

import com.minis.aop.springframework.aop.BeforeAdvice;
import com.minis.aop.springframework.aop.MethodBeforeAdvice;
import com.minis.aop.aopalliance.intercept.MethodInterceptor;
import com.minis.aop.aopalliance.intercept.MethodInvocation;

public class MethodBeforeAdviceInterceptor implements MethodInterceptor, BeforeAdvice {

    private final MethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object target = invocation.getThis();
        this.advice.before(invocation.getMethod(), invocation.getArguments(), target);
        return invocation.proceed();
    }
}
