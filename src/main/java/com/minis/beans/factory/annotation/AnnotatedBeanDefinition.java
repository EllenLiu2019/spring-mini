package com.minis.beans.factory.annotation;

import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.MethodMetadata;

public interface AnnotatedBeanDefinition {

    AnnotationMetadata getMetadata();

    void setPrimary(boolean b);

    MethodMetadata getFactoryMethodMetadata();
}
