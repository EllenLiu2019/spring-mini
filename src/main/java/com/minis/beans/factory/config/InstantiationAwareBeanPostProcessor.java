package com.minis.beans.factory.config;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValues;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor{
    default Object postProcessBeforeInstantiation(Object bean, String beanName) {
        return null;
    }

    default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
            throws BeansException {

        return pvs;
    }
}
