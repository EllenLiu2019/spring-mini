package com.minis.beans.factory.annotation;

import com.minis.beans.factory.config.BeanDefinition;
import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.StandardAnnotationMetadata;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnnotatedGenericBeanDefinition extends BeanDefinition implements AnnotatedBeanDefinition {

    private final AnnotationMetadata metadata;
    private final Map<String, Object> attributes = new LinkedHashMap<>();

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

    public void setAttribute(String name, Object value) {
        if (value != null) {
            this.attributes.put(name, value);
        }
        else {
            removeAttribute(name);
        }
    }

    public Object removeAttribute(String name) {
        return this.attributes.remove(name);
    }

}
