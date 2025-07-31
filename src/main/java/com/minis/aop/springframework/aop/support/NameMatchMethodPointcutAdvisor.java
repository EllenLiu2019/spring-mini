package com.minis.aop.springframework.aop.support;

import com.minis.aop.aopalliance.aop.Advice;
import com.minis.aop.springframework.aop.*;
import com.minis.aop.aopalliance.intercept.MethodInterceptor;
import com.minis.aop.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor;
import com.minis.aop.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import com.minis.beans.factory.annotation.Autowired;

// TODO: 代替了 DefaultAdvisor， 新增了 pointcut 功能；即，批量匹配增强方法
public class NameMatchMethodPointcutAdvisor implements PointcutAdvisor {

    @Autowired
    private Advice advice;
    private String mappedName = "do*";

    @Autowired
    private Pointcut pointcut;

    @Autowired
    private MethodInterceptor methodInterceptor;

    public void setAdvice(Advice advice) {
        this.advice = advice;

        MethodInterceptor interceptor = null;
        if (advice instanceof BeforeAdvice) {
            interceptor = new MethodBeforeAdviceInterceptor((MethodBeforeAdvice) advice);
        } else if (advice instanceof AfterAdvice) {
            interceptor = new AfterReturningAdviceInterceptor((AfterReturningAdvice) advice);
        } else if (advice instanceof MethodInterceptor) {
            interceptor = (MethodInterceptor) advice;
        }
        this.methodInterceptor = interceptor;
    }
    public void setMappedName(String mappedName) {
        this.mappedName = mappedName;
    }

    @Override
    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    @Override
    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }
}
