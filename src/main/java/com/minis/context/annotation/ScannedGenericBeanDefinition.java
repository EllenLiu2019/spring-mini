package com.minis.context.annotation;

import com.minis.beans.factory.annotation.AnnotatedBeanDefinition;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.MethodMetadata;
import com.minis.core.type.classreading.MetadataReader;

public class ScannedGenericBeanDefinition extends BeanDefinition implements AnnotatedBeanDefinition {

    private final AnnotationMetadata metadata;

    public ScannedGenericBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
        this.metadata = AnnotationMetadata.introspect(beanClass);
        setBeanClassName(this.metadata.getClassName());
    }

    public ScannedGenericBeanDefinition(MetadataReader metadataReader) {
        this.metadata = metadataReader.getAnnotationMetadata();
        setBeanClassName(this.metadata.getClassName());
        setResource(metadataReader.getResource());
    }

    public void setBeanClassName(String className) {
        setClassName(className);
    }

    @Override
    public AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public MethodMetadata getFactoryMethodMetadata() {
        return null;
    }

}
