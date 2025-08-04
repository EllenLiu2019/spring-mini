package com.minis.beans.factory.support;


import com.minis.beans.factory.BeanFactory;
import com.minis.beans.BeansException;
import com.minis.beans.factory.config.DependencyDescriptor;

import java.util.Set;

public interface AutowireCapableBeanFactory extends BeanFactory {
    int AUTOWIRE_NO = 0;

    int AUTOWIRE_BY_NAME = 1;

    int AUTOWIRE_BY_TYPE = 2;

    int AUTOWIRE_CONSTRUCTOR = 3;

    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException, ReflectiveOperationException;

    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException;

    Object resolveDependency(DependencyDescriptor desc, String beanName, Set<String> autowiredBeanNames);

}
