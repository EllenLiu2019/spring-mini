package com.minis.context.annotation;

import com.minis.beans.factory.annotation.AnnotatedBeanDefinition;
import com.minis.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.BeanNameGenerator;
import com.minis.beans.factory.support.RootBeanDefinition;
import com.minis.core.annotation.AnnotationAttributes;
import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.MethodMetadata;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ConfigurationClassBeanDefinitionReader {

    private final BeanDefinitionRegistry registry;

    private final BeanNameGenerator importBeanNameGenerator;


    ConfigurationClassBeanDefinitionReader(BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        this.registry = registry;
        this.importBeanNameGenerator = importBeanNameGenerator;
    }

    public void loadBeanDefinitions(Set<ConfigurationClass> configurationModel) {
        for (ConfigurationClass configClass : configurationModel) {
            loadBeanDefinitionsForConfigurationClass(configClass);
        }
    }

    private void loadBeanDefinitionsForConfigurationClass(ConfigurationClass configClass) {
        if (configClass.isImported()) {
            registerBeanDefinitionForImportedConfigurationClass(configClass);
        }

        for (BeanMethod beanMethod : configClass.getBeanMethods()) {
            loadBeanDefinitionsForBeanMethod(beanMethod);
        }

        loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars());
    }

    private void loadBeanDefinitionsFromRegistrars(Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> registrars) {
        registrars.forEach((registrar, metadata) ->
                registrar.registerBeanDefinitions(metadata, this.registry, this.importBeanNameGenerator));
    }

    private void loadBeanDefinitionsForBeanMethod(BeanMethod beanMethod) {
        ConfigurationClass configClass = beanMethod.getConfigurationClass();
        MethodMetadata metadata = beanMethod.getMetadata();
        String methodName = metadata.getMethodName();

        AnnotationAttributes bean = AnnotationConfigUtils.attributesFor(metadata, Bean.class);

        // Consider name and any aliases
        List<String> names = new ArrayList<>(Arrays.asList(bean.getStringArray("value")));
        String beanName = (!names.isEmpty() ? names.remove(0) : methodName);

        ConfigurationClassBeanDefinition beanDef = new ConfigurationClassBeanDefinition(configClass, metadata);

        if (metadata.isStatic()) {
            // static @Bean method
            throw new IllegalStateException("@Bean method '" + methodName + "' must not be static");
        } else {
            // instance @Bean method
            beanDef.setFactoryBeanName(configClass.getBeanName());
            beanDef.setUniqueFactoryMethodName(methodName);
        }

        beanDef.setAutowireMode(BeanDefinition.AUTOWIRE_CONSTRUCTOR);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(beanDef, metadata);

        this.registry.registerBeanDefinition(beanName, beanDef);
    }

    private void registerBeanDefinitionForImportedConfigurationClass(ConfigurationClass configClass) {
        AnnotationMetadata metadata = configClass.getMetadata();
        AnnotatedGenericBeanDefinition configBeanDef = new AnnotatedGenericBeanDefinition(metadata);

        String configBeanName = this.importBeanNameGenerator.generateBeanName(configBeanDef, this.registry);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(configBeanDef, metadata);

        this.registry.registerBeanDefinition(configBeanName, configBeanDef);
        configClass.setBeanName(configBeanName);

        log.trace("Registered bean definition for imported class '" + configBeanName + "'");
    }

    private static class ConfigurationClassBeanDefinition extends RootBeanDefinition implements AnnotatedBeanDefinition {

        private final AnnotationMetadata annotationMetadata;

        private final MethodMetadata factoryMethodMetadata;


        public ConfigurationClassBeanDefinition(ConfigurationClass configClass, MethodMetadata beanMethodMetadata) {
            this.annotationMetadata = configClass.getMetadata();
            this.factoryMethodMetadata = beanMethodMetadata;
        }

        @Override
        public AnnotationMetadata getMetadata() {
            return this.annotationMetadata;
        }

        public MethodMetadata getFactoryMethodMetadata() {
            return this.factoryMethodMetadata;
        }
    }
}
