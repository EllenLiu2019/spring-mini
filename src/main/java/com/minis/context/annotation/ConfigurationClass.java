package com.minis.context.annotation;

import com.minis.core.io.Resource;
import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.classreading.MetadataReader;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class ConfigurationClass {

    private final AnnotationMetadata metadata;

    private Resource resource;

    private String beanName;

    private final Set<BeanMethod> beanMethods = new LinkedHashSet<>();

    private final Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> importBeanDefinitionRegistrars =
            new LinkedHashMap<>();

    private final Set<ConfigurationClass> importedBy = new LinkedHashSet<>(1);

    ConfigurationClass(Class<?> clazz, ConfigurationClass importedBy) {
        this.metadata = AnnotationMetadata.introspect(clazz);
        this.importedBy.add(importedBy);
    }

    ConfigurationClass(MetadataReader metadataReader, ConfigurationClass importedBy) {
        this.metadata = metadataReader.getAnnotationMetadata();
        this.resource = metadataReader.getResource();
        this.importedBy.add(importedBy);
    }

    ConfigurationClass(AnnotationMetadata metadata, String beanName) {
        this.metadata = metadata;
        this.beanName = beanName;
    }

    AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    void addImportBeanDefinitionRegistrar(ImportBeanDefinitionRegistrar registrar, AnnotationMetadata importingClassMetadata) {
        this.importBeanDefinitionRegistrars.put(registrar, importingClassMetadata);
    }

    Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> getImportBeanDefinitionRegistrars() {
        return this.importBeanDefinitionRegistrars;
    }

    boolean isImported() {
        return !this.importedBy.isEmpty();
    }

    void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    void addBeanMethod(BeanMethod method) {
        this.beanMethods.add(method);
    }

    Set<BeanMethod> getBeanMethods() {
        return this.beanMethods;
    }

    String getBeanName() {
        return this.beanName;
    }
}
