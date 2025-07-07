package com.minis.beans.factory.config;

import com.minis.beans.BeanFactory;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(BeanFactory beanFactory);
}
