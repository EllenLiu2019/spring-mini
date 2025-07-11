package com.minis.beans.factory.config;

import com.minis.beans.factory.BeanFactory;
import com.minis.beans.BeansException;

public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException, ReflectiveOperationException;
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
    void setBeanFactory(BeanFactory beanFactory);
}
