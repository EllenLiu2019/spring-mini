package com.minis.beans.factory.config;

import com.minis.beans.BeansException;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor{
    default Object postProcessBeforeInstantiation(Object bean, String beanName) {
        return null;
    }

    default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }
}
