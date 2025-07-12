package com.minis.aop;

public class AfterReturningAdviceInterceptor implements MethodInterceptor, AfterAdvice {
    private final AfterReturningAdvice afterReturningAdvice;

    public AfterReturningAdviceInterceptor(AfterReturningAdvice afterReturningAdvice) {
        this.afterReturningAdvice = afterReturningAdvice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();
        Object target = invocation.getThis();
        this.afterReturningAdvice.afterReturning(retVal, invocation.getMethod(), invocation.getArguments(), target);
        return retVal;
    }
}
