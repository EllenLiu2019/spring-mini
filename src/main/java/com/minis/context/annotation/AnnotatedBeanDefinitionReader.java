package com.minis.context.annotation;

import com.minis.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.BeanNameGenerator;

public class AnnotatedBeanDefinitionReader {

    private final BeanDefinitionRegistry registry;

    private BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    }

    /**
     * 	 Register one or more component classes to be processed.
     * 	 <p>Calls to {@code register} are idempotent; adding the same
     * 	 component class more than once has no additional effect.
     * 	 @param componentClasses one or more component classes,
     * 	 for example, {@link Configuration @Configuration} classes
     */
    public void register(Class<?>... componentClasses) {
        for (Class<?> componentClass : componentClasses) {
            registerBean(componentClass);
        }
    }

    public void registerBean(Class<?> beanClass) {
        doRegisterBean(beanClass);
    }

    /**
     * TODO: Register a bean from the given bean class, deriving its metadata from class-declared annotations.
     * @param beanClass - the class of the bean
     */
    private void doRegisterBean(Class<?> beanClass) {
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
        abd.setAttribute(ConfigurationClassUtils.CANDIDATE_ATTRIBUTE, Boolean.TRUE);
        String beanName = this.beanNameGenerator.generateBeanName(abd, this.registry);

        //abd.setAttribute(ConfigurationClassUtils.CANDIDATE_ATTRIBUTE, Boolean.TRUE);
        /* TODO: have a chance to generate proxy
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(definitionHolder, this.registry);
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);*/

        this.registry.registerBeanDefinition(beanName, abd);
    }
}
