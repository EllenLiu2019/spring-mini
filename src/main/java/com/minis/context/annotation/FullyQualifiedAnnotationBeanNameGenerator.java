package com.minis.context.annotation;

import com.minis.beans.factory.config.BeanDefinition;

public class FullyQualifiedAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {

    public static final FullyQualifiedAnnotationBeanNameGenerator INSTANCE =
            new FullyQualifiedAnnotationBeanNameGenerator();


    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        return definition.getBeanClassName();
    }
}
