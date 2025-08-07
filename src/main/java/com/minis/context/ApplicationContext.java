package com.minis.context;

import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.ListableBeanFactory;
import com.minis.core.env.ConfigurableEnvironment;
import com.minis.core.env.EnvironmentCapable;

public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, ApplicationEventPublisher {
    String getApplicationName();
    long getStartupDate();
    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
    void setEnvironment(ConfigurableEnvironment environment);
    void close();
    boolean isActive();
}
