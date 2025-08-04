package com.minis.context;

import com.minis.beans.factory.support.ConfigurableBeanFactory;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.ListableBeanFactory;
import com.minis.core.env.Environment;
import com.minis.core.env.EnvironmentCapable;

public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, ApplicationEventPublisher {
    String getApplicationName();
    long getStartupDate();
    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
    void setEnvironment(Environment environment);
    //void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanPostProcessor);
    //void refresh();
    void close();
    boolean isActive();
}
