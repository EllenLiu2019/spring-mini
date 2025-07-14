package com.minis.aop.springframework.aop.framework.adapter;

import com.minis.aop.springframework.aop.AfterAdvice;
import com.minis.aop.springframework.aop.AfterReturningAdvice;
import com.minis.aop.aopalliance.intercept.MethodInterceptor;
import com.minis.aop.aopalliance.intercept.MethodInvocation;

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
