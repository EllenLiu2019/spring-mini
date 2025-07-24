package com.minis.beans.factory.config;

import com.minis.beans.factory.BeanFactory;

@FunctionalInterface
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(BeanFactory beanFactory);
}
