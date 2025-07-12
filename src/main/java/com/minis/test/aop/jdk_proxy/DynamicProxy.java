package com.minis.test.aop.jdk_proxy;

import com.minis.test.aop.service.IAction;
import com.minis.test.aop.service.RealAction;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

// TODO：used for showing the underlying implementation of "JDK Proxy"
//   Also, Spring AOP will take "JDK Proxy" advantage to generate proxy in the process of creating bean.
@Slf4j
public class DynamicProxy {
    private final Object subject;

    public DynamicProxy(Object subject) {
        this.subject = subject;
    }

    public Object getProxy() {
        Object proxy = Proxy.newProxyInstance(DynamicProxy.class.getClassLoader(), subject.getClass().getInterfaces(), handler());
        return proxy;
    }

    private InvocationHandler handler() {
        return (proxy, method, args) -> {
            if (method.getName().equals("doAction")) {
                log.info("before call real object.....");
                return method.invoke(subject, args);
            }
            return null;
        };
    }

    public static void main(String[] args) {
        IAction action = new RealAction();
        DynamicProxy proxy = new DynamicProxy(action);
        IAction proxyInstance = (IAction) proxy.getProxy();
        proxyInstance.doAction();
    }

}
