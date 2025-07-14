package com.minis.aop.springframework.aop.support;

import com.minis.aop.springframework.aop.Advisor;
import com.minis.aop.aopalliance.intercept.MethodInterceptor;

// 中间过程，被 NameMatchMethodPointcutAdvisor 代替，弃用
@Deprecated
public class DefaultAdvisor implements Advisor {
    private MethodInterceptor methodInterceptor;
    @Override
    public MethodInterceptor getMethodInterceptor() {
        return this.methodInterceptor;
    }

    @Override
    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }
}
