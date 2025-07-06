package com.minis.beans;


public interface BeanFactory {
    Object getBean(String beanName) throws BeansException, ReflectiveOperationException;

    // TODO: why need this method?
    void registerBean(String beanName, Object obj);
    boolean containsBean(String beanName);
    boolean isSingleton(String beanName);
    boolean isPrototype(String beanName);
    Class<?> getType(String beanName);
}
