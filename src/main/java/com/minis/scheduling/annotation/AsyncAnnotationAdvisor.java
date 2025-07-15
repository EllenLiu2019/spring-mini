package com.minis.scheduling.annotation;

import com.minis.aop.aopalliance.intercept.MethodInterceptor;
import com.minis.aop.springframework.aop.Advisor;
import com.minis.aop.springframework.aop.interceptor.AsyncExecutionInterceptor;

public class AsyncAnnotationAdvisor implements Advisor {

    private MethodInterceptor methodInterceptor;

    @Override
    public MethodInterceptor getMethodInterceptor() {
        return this.methodInterceptor;
    }

    @Override
    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public void setMethodInterceptor(AsyncExecutionInterceptor methodInterceptor) {
        this.setMethodInterceptor((MethodInterceptor) methodInterceptor);
    }
}
