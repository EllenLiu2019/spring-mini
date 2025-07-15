package com.minis.aop.springframework.aop.framework;

import com.minis.aop.aopalliance.intercept.MethodInterceptor;
import com.minis.aop.aopalliance.intercept.MethodInvocation;
import com.minis.aop.springframework.aop.Advisor;
import com.minis.aop.springframework.aop.PointcutAdvisor;
import com.minis.scheduling.annotation.AsyncAnnotationAdvisor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// TODO: Spring 对 【JDK 动态代理】的实现类
//  该类也是一个【InvocationHandler】
@Slf4j
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private final Object target;
    private final Advisor advisor;

    public JdkDynamicAopProxy(Object target, Advisor advisor) {
        this.target = target;
        this.advisor = advisor;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(JdkDynamicAopProxy.class.getClassLoader(), target.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> targetClass = target != null ? target.getClass() : null;
        MethodInterceptor interceptor = advisor.getMethodInterceptor();
        MethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target, method, args);

        if (this.advisor instanceof PointcutAdvisor pointcutAdvisor) {
            if (pointcutAdvisor.getPointcut().getMethodMatcher().matches(method, targetClass)) {
                return interceptor.invoke(invocation);
            }
        }

        if (this.advisor instanceof AsyncAnnotationAdvisor) {
            Object result = interceptor.invoke(invocation);
            log.info("invoke finished, result = {}", result);
            return result;
        }

        return null;
    }

}
