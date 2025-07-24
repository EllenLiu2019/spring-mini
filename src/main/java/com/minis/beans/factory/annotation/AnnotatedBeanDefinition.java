package com.minis.beans.factory.annotation;

import com.minis.core.type.AnnotationMetadata;

public interface AnnotatedBeanDefinition {

    AnnotationMetadata getMetadata();

    void setPrimary(boolean b);
}
