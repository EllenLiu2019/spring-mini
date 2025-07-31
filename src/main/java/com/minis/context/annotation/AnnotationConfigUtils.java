package com.minis.context.annotation;

import com.minis.beans.factory.annotation.AnnotatedBeanDefinition;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.beans.factory.support.RootBeanDefinition;
import com.minis.context.support.GenericApplicationContext;
import com.minis.core.annotation.AnnotationAttributes;
import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.MethodMetadata;

import java.util.Set;

public abstract class AnnotationConfigUtils {
    public static final String CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.minis.context.annotation.internalConfigurationAnnotationProcessor";

    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.com.context.annotation.internalAutowiredAnnotationProcessor";


    /**
     * TODO: Register all relevant annotation-post-processors in the given registry.
     * @Params: registry – the registry to operate on
     */
    public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
        registerAnnotationConfigProcessors(registry, null);
    }

    public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry, Object source) {
        DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(registry);
        if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
            registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME);
        }
        if (!registry.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
            registerPostProcessor(registry, def, AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME);
        }
    }

    private static DefaultListableBeanFactory unwrapDefaultListableBeanFactory(BeanDefinitionRegistry registry) {
        if (registry instanceof DefaultListableBeanFactory dlbf) {
            return dlbf;
        }
        else if (registry instanceof GenericApplicationContext gac) {
            return gac.getDefaultListableBeanFactory();
        }
        else {
            return null;
        }
    }

    private static void registerPostProcessor(BeanDefinitionRegistry registry, BeanDefinition definition, String beanName) {
        registry.registerBeanDefinition(beanName, definition);
    }

    public static Set<AnnotationAttributes> attributesForRepeatable(AnnotationMetadata metadata,
                                                                    Class<ComponentScan> componentScanClass) {
        return metadata.getAnnotationAttributes(componentScanClass);
    }

    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
        processCommonDefinitionAnnotations(abd, abd.getMetadata());
    }

    static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotationMetadata metadata) {
        if (metadata.isAnnotated(Primary.class.getName())) {
            abd.setPrimary(true);
        }
    }

    public static AnnotationAttributes attributesFor(MethodMetadata metadata, Class<Bean> annotationType) {
        return AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(annotationType.getName()));
    }
}
