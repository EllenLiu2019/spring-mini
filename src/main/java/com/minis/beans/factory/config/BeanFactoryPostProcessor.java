package com.minis.beans.factory.config;

import com.minis.beans.factory.support.ConfigurableListableBeanFactory;

@FunctionalInterface
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
}
