package com.minis.beans.factory.support;

import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanPostProcessor;

public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

    void postProcessMergedBeanDefinition(BeanDefinition beanDefinition, String beanName);
}
