package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.FactoryBean;

public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry{

    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName) {
        Object object;
        try {
            object = factory.getObject();
            object = postProcessObjectFromFactoryBean(object, beanName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
        return object;
    }

    protected Class<?> getTypeForFactoryBean(final FactoryBean<?> factoryBean) {
        return factoryBean.getObjectType();
    }
}
