package com.minis.context.annotation;

import com.minis.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.BeanNameGenerator;
import com.minis.core.type.AnnotationMetadata;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

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

        loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars());
    }

    private void loadBeanDefinitionsFromRegistrars(Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> registrars) {
        registrars.forEach((registrar, metadata) ->
                registrar.registerBeanDefinitions(metadata, this.registry, this.importBeanNameGenerator));
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
}
