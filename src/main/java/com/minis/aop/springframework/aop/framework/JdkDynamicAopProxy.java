package com.minis.aop.springframework.aop.framework;

import com.minis.aop.aopalliance.intercept.MethodInterceptor;
import com.minis.aop.aopalliance.intercept.MethodInvocation;
import com.minis.aop.springframework.aop.PointcutAdvisor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// TODO: Spring 对 【JDK 动态代理】的实现类
//  该类也是一个【InvocationHandler】
@Slf4j
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private final Object target;
    private final PointcutAdvisor advisor;

    public JdkDynamicAopProxy(Object target, PointcutAdvisor advisor) {
        this.target = target;
        this.advisor = advisor;
    }

    @Override
    public Object getProxy() {
        log.debug("creating proxy instance for target={}", target);
        Object obj = Proxy.newProxyInstance(JdkDynamicAopProxy.class.getClassLoader(), target.getClass().getInterfaces(), this);
        log.debug("proxy instance={} created", obj.getClass());
        return obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> targetClass = target != null ? target.getClass() : null;
        if (this.advisor.getPointcut().getMethodMatcher().matches(method, targetClass)) {
            MethodInterceptor interceptor = advisor.getMethodInterceptor();
            MethodInvocation methodInvocation = new ReflectiveMethodInvocation(proxy, target, method, args);
            return interceptor.invoke(methodInvocation);
        }
        return null;
    }

}
