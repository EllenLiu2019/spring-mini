package com.minis.beans.factory.support;

import com.minis.beans.factory.config.BeanDefinition;

import java.lang.reflect.AnnotatedElement;

public class RootBeanDefinition extends BeanDefinition {

    boolean isFactoryMethodUnique;

    public RootBeanDefinition() {
    }

    public RootBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
    }


    public void setUniqueFactoryMethodName(String name) {
        setFactoryMethodName(name);
        this.isFactoryMethodUnique = true;
    }


}
