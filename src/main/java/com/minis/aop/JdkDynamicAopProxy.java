package com.minis.aop;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// TODO: Spring 对 【JDK 动态代理】的实现类
//  该类也是一个【InvocationHandler】
@Slf4j
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private final Object target;

    public JdkDynamicAopProxy(Object target) {
        this.target = target;
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
        if (method.getName().equals("doAction")) {
            log.info("before calling real object, dynamic proxy invoked");
            return method.invoke(target, args);
        }
        return null;
    }

}
