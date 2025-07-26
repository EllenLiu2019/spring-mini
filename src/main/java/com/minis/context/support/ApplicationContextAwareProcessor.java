package com.minis.context.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.context.ApplicationContextAware;
import com.minis.context.ConfigurableApplicationContext;

public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    private final ConfigurableApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException, ReflectiveOperationException {
        if (bean instanceof ApplicationContextAware applicationContextAware) {
            applicationContextAware.setApplicationContext(this.applicationContext);
        }
        return bean;
    }
}
