package com.minis.context.annotation;

import com.minis.beans.factory.annotation.AnnotatedBeanDefinition;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.core.type.AnnotationMetadata;

public class ScannedGenericBeanDefinition extends BeanDefinition implements AnnotatedBeanDefinition {

    private final AnnotationMetadata metadata;

    public ScannedGenericBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
        this.metadata = AnnotationMetadata.introspect(beanClass);
        setBeanClassName(this.metadata.getClassName());
    }

    private void setBeanClassName(String className) {
        setClassName(className);
    }

    @Override
    public AnnotationMetadata getMetadata() {
        return this.metadata;
    }

}
