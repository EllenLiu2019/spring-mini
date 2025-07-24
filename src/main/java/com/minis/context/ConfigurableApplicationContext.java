package com.minis.context;

import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.context.event.ApplicationListener;

public interface ConfigurableApplicationContext extends ApplicationContext {
    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

    void addApplicationListener(ApplicationListener<?> listener);

    void refresh() throws IllegalStateException;

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}
