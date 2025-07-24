package com.minis.beans.factory.support;

import com.minis.beans.factory.config.BeanDefinition;

public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
    void removeBeanDefinition(String beanName) throws Exception;
    BeanDefinition getBeanDefinition(String beanName);
    boolean containsBeanDefinition(String beanName);

    String[] getBeanDefinitionNames();
}
