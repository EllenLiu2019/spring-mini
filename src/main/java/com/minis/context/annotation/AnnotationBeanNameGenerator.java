package com.minis.context.annotation;

import com.minis.beans.factory.annotation.AnnotatedBeanDefinition;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.BeanNameGenerator;
import com.minis.core.annotation.MergedAnnotation;
import com.minis.core.type.AnnotationMetadata;
import com.minis.stereotype.Component;
import com.minis.utils.ClassUtils;
import com.minis.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationBeanNameGenerator implements BeanNameGenerator {

    public static final AnnotationBeanNameGenerator INSTANCE = new AnnotationBeanNameGenerator();

    private static final String COMPONENT_ANNOTATION_CLASSNAME = "org.springframework.stereotype.Component";

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        if (definition instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
            String beanName = determineBeanNameFromAnnotation(annotatedBeanDefinition);
            if (StringUtils.hasText(beanName)) {
                // Explicit bean name found.
                return beanName;
            }
        }
        return this.buildDefaultBeanName(definition);
    }

    private String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
        AnnotationMetadata metadata = annotatedDef.getMetadata();
        return getExplicitBeanName(metadata);
    }

    private String getExplicitBeanName(AnnotationMetadata metadata) {
        MergedAnnotation<Annotation> aggregates = metadata.getAnnotations().getAggregates(Component.class);
        if(aggregates == null) return null;
        Object value = aggregates.asAnnotationAttributes().get(MergedAnnotation.VALUE);
        if (value instanceof String name) {
            return name;
        }
        return null;
    }

    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        String shortClassName = ClassUtils.getShortName(beanClassName);
        return StringUtils.uncapitalizeAsProperty(shortClassName);
    }
}
