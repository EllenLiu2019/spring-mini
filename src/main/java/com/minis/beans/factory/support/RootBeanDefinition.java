package com.minis.beans.factory.support;

import com.minis.beans.factory.config.BeanDefinition;

import java.lang.reflect.AnnotatedElement;

public class RootBeanDefinition extends BeanDefinition {

    public RootBeanDefinition() {
    }

    public RootBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
    }


}
