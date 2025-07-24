package com.minis.beans.factory.config;

public class BeanDefinitionHolder {

    private final BeanDefinition beanDefinition;

    private final String beanName;


    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
    }


    public String getBeanName() {
        return beanName;
    }

    public BeanDefinition getBeanDefinition() {
        return beanDefinition;
    }
}
