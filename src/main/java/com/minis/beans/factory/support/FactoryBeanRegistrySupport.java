package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.FactoryBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry{
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);

    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName) {
        Object object;
        try {
            object = factory.getObject();
            object = postProcessObjectFromFactoryBean(object, beanName);
            this.factoryBeanObjectCache.put(beanName, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    protected Object getCachedObjectForFactoryBean(String beanName) {
        return this.factoryBeanObjectCache.get(beanName);
    }

    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
        return object;
    }

    protected Class<?> getTypeForFactoryBean(final FactoryBean<?> factoryBean) {
        return factoryBean.getObjectType();
    }
}
