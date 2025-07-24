package com.minis.beans.factory;


import com.minis.beans.BeansException;

public interface BeanFactory {
    Object getBean(String beanName) throws BeansException, ReflectiveOperationException;
    boolean containsBean(String beanName);
    boolean isSingleton(String beanName);
    boolean isPrototype(String beanName);
    Class<?> getType(String beanName);


}
