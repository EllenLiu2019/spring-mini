package com.minis.context.annotation;

import com.minis.beans.factory.annotation.AnnotatedBeanDefinition;
import com.minis.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.support.RootBeanDefinition;
import com.minis.core.annotation.AnnotationAttributes;
import com.minis.core.annotation.MergedAnnotations;
import com.minis.core.annotation.TypeMappedAnnotations;
import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.StandardAnnotationMetadata;
import com.minis.core.type.classreading.MetadataReaderFactory;
import com.minis.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class ConfigurationClassUtils {

    static final String CONFIGURATION_CLASS_FULL = "full";

    static final String CONFIGURATION_CLASS_LITE = "lite";

    static final String CANDIDATE_ATTRIBUTE = ConfigurationClassPostProcessor.class.getName() + ".candidate";
    static final String CONFIGURATION_CLASS_ATTRIBUTE = ConfigurationClassPostProcessor.class.getName() + ".configurationClass";

    private static final Set<String> candidateIndicators = Set.of(Component.class.getName(), ComponentScan.class.getName(), Import.class.getName());

    public static boolean checkConfigurationClassCandidate(BeanDefinition beanDef) {
        String className = beanDef.getBeanClassName();
        if (className == null || beanDef.getFactoryMethodName() != null) {
            return false;
        }

        AnnotationMetadata metadata = null;

        if (beanDef instanceof AnnotatedBeanDefinition annotatedBd && className.equals(annotatedBd.getMetadata().getClassName())) {
            metadata = annotatedBd.getMetadata();
        } else if (beanDef instanceof RootBeanDefinition rootBeanDef && rootBeanDef.hasBeanClass()) {
            Class<?> beanClass = rootBeanDef.getBeanClass();
            if (BeanFactoryPostProcessor.class.isAssignableFrom(beanClass) ||
                    BeanPostProcessor.class.isAssignableFrom(beanClass)) {
                return false;
            }
        } else {
            try {
                Class<?> beanClass = Class.forName(className);
                metadata = StandardAnnotationMetadata.from(beanClass);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        AnnotationAttributes config = metadata.getAnnotationAttributes(Configuration.class.getName());
        if (config != null && !Boolean.FALSE.equals(config.get("proxyBeanMethods"))) {
            return true;
        } /*else if (config != null || isConfigurationCandidate(metadata)) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
        }*/ else {
            return false;
        }
    }

    /*static boolean isConfigurationCandidate(AnnotationMetadata metadata) {
        // Do not consider an interface or an annotation...
        if (metadata.isInterface()) {
            return false;
        }

        // Any of the typical annotations found?
        for (String indicator : candidateIndicators) {
            if (metadata.isAnnotated(indicator)) {
                return true;
            }
        }

        // Finally, let's look for @Bean methods...
        return hasBeanMethods(metadata);
    }

    private static boolean hasBeanMethods(AnnotationMetadata metadata) {
       return metadata.hasAnnotatedMethods(Bean.class.getName());
    }*/
}
