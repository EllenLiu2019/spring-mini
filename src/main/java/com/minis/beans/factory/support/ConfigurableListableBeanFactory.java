package com.minis.beans.factory.support;


import com.minis.beans.BeansException;
import com.minis.beans.factory.config.BeanDefinition;

public interface ConfigurableListableBeanFactory
        extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {
    void preInstantiateSingletons();

    BeanDefinition getBeanDefinition(String beanName) throws BeansException;
}
