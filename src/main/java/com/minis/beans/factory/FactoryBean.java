package com.minis.beans.factory;

public interface FactoryBean<T> {

    // TODO: core method, 从 Factory Bean 中获取内部包含的代理对象,
    //  实现类在 aop package 中的 ProxyFactoryBean 中；
    T getObject() throws Exception;
    Class<?> getObjectType();
    default boolean isSingleton() {
        return true;
    }
}
