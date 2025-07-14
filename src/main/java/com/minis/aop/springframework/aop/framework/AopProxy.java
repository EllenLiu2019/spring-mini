package com.minis.aop.springframework.aop.framework;

// TODO: Spring 对【动态代理】提供的封装，
//  动态代理技术包含： JDK & Cglib & AspectJ(编译创建代理，性能更好)
//  Spring 自身动态代理实现：JDK & Cglib
//  Spring 集成了 AspectJ 的部分功能, （eg. @Aspect\@Pointcut）；需要引入 AspectJ 的相关依赖（如 aspectjweaver)，但不需要完整引入整个 AspectJ 框架，
//  JDK 动态代理实现类：JdkDynamicAopProxy
public interface AopProxy {
    Object getProxy();
}
