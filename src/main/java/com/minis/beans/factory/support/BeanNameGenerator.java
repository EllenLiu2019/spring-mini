package com.minis.beans.factory.support;

import com.minis.beans.factory.config.BeanDefinition;

public interface BeanNameGenerator {

    String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry);
}
