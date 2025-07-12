package com.minis.aop;

// TODO: Spring 对 【动态代理工厂】 提供的封装，
//  动态代理技术包含： JDK 动态代理
public interface AopProxyFactory {
    AopProxy createAopProxy(Object target);
}
