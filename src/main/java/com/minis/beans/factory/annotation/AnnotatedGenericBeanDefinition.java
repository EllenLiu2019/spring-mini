package com.minis.beans.factory.annotation;

import com.minis.beans.factory.config.BeanDefinition;
import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.MethodMetadata;
import com.minis.core.type.StandardAnnotationMetadata;


public class AnnotatedGenericBeanDefinition extends BeanDefinition implements AnnotatedBeanDefinition {

    private final AnnotationMetadata metadata;

    private MethodMetadata factoryMethodMetadata;

    public AnnotatedGenericBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
        this.metadata = AnnotationMetadata.introspect(beanClass);
    }

    public AnnotatedGenericBeanDefinition(AnnotationMetadata metadata) {
        if (metadata instanceof StandardAnnotationMetadata sam) {
            setBeanClass(sam.getIntrospectedClass());
        }
        this.metadata = metadata;
    }

    public AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public MethodMetadata getFactoryMethodMetadata() {
        return this.factoryMethodMetadata;
    }

}
