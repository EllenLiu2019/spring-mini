package com.minis.aop;

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
