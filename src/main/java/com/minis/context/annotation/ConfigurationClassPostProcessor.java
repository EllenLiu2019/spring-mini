package com.minis.context.annotation;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.annotation.AnnotatedBeanDefinition;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanDefinitionHolder;
import com.minis.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.minis.beans.factory.support.BeanNameGenerator;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.MethodMetadata;
import com.minis.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * TODO: Registered by AnnotationConfigUtils.class
 */
@Slf4j
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {

    public static final AnnotationBeanNameGenerator IMPORT_BEAN_NAME_GENERATOR =
            FullyQualifiedAnnotationBeanNameGenerator.INSTANCE;

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    private final Set<Integer> factoriesPostProcessed = new HashSet<>();

    private final Set<Integer> registriesPostProcessed = new HashSet<>();


    private ConfigurationClassBeanDefinitionReader reader;
    private BeanNameGenerator importBeanNameGenerator = IMPORT_BEAN_NAME_GENERATOR;

    /**
     * Prepare the Configuration classes for servicing bean requests at runtime
     * by replacing them with CGLIB-enhanced subclasses.
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        int factoryId = System.identityHashCode(beanFactory);
        this.factoriesPostProcessed.add(factoryId);
        if (!this.registriesPostProcessed.contains(factoryId)) {
            // BeanDefinitionRegistryPostProcessor hook apparently not supported...
            // Simply call processConfigurationClasses lazily at this point then.
            processConfigBeanDefinitions((BeanDefinitionRegistry) beanFactory);
        }

        enhanceConfigurationClasses(beanFactory);
        beanFactory.addBeanPostProcessor(new ImportAwareBeanPostProcessor(beanFactory));
    }

    // any candidates are then enhanced by a ConfigurationClassEnhancer.
    private void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanDefinition> configBeanDefs = new LinkedHashMap<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
            Object configClassAttr = beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE);
            AnnotationMetadata annotationMetadata = null;
            MethodMetadata methodMetadata = null;
            if (beanDef instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
                annotationMetadata = annotatedBeanDefinition.getMetadata();
                methodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();
            }

            if ((configClassAttr != null || methodMetadata != null) && !beanDef.hasBeanClass()) {
                // Configuration class (full or lite) or a configuration-derived @Bean method
                // -> eagerly resolve bean class at this point, unless it's a 'lite' configuration
                // or component class without @Bean methods.
                boolean liteConfigurationCandidateWithoutBeanMethods =
                        (ConfigurationClassUtils.CONFIGURATION_CLASS_LITE.equals(configClassAttr) &&
                                annotationMetadata != null && !ConfigurationClassUtils.hasBeanMethods(annotationMetadata));
                if (!liteConfigurationCandidateWithoutBeanMethods) {
                    try {
                        beanDef.resolveBeanClass(this.beanClassLoader);
                    } catch (Throwable ex) {
                        throw new IllegalStateException("Cannot load configuration class: " + beanDef.getBeanClassName(), ex);
                    }
                }
            }

            if (ConfigurationClassUtils.CONFIGURATION_CLASS_FULL.equals(configClassAttr)) {
                if (beanFactory.containsSingleton(beanName)) {
                    log.warn("Cannot enhance @Configuration bean definition '" + beanName +
                            "' since its singleton instance has been created too early. The typical cause " +
                            "is a non-static @Bean method with a BeanDefinitionRegistryPostProcessor " +
                            "return type: Consider declaring such methods as 'static' and/or marking the " +
                            "containing configuration class as 'proxyBeanMethods=false'.");
                } else {
                    configBeanDefs.put(beanName, beanDef);
                }
            }
        }
        if (configBeanDefs.isEmpty()) {
            return;
        }
        ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
        for (Map.Entry<String, BeanDefinition> entry : configBeanDefs.entrySet()) {
            BeanDefinition beanDef = entry.getValue();
            // If a @Configuration class gets proxied, always proxy the target class
            // Set enhanced subclass of the user-specified bean class
            Class<?> configClass = beanDef.getBeanClass();
            Class<?> enhancedClass = enhancer.enhance(configClass);
            if (configClass != enhancedClass) {
                log.trace(String.format("Replacing bean definition '%s' existing class '%s' with " +
                        "enhanced class '%s'", entry.getKey(), configClass.getName(), enhancedClass.getName()));
                beanDef.setBeanClass(enhancedClass);
            }
        }

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        int registryId = System.identityHashCode(registry);
        this.registriesPostProcessed.add(registryId);
        processConfigBeanDefinitions(registry);
    }

    private void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
        String[] candidateNames = registry.getBeanDefinitionNames();
        for (String beanName : candidateNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef)) {
                configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
            }
        }

        // Return immediately if no @Configuration classes were found
        if (configCandidates.isEmpty()) {
            return;
        }

        // Parse each @Configuration class
        ConfigurationClassParser parser = new ConfigurationClassParser(registry);

        Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
        parser.parse(candidates);

        Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());

        if (this.reader == null) {
            this.reader = new ConfigurationClassBeanDefinitionReader(registry, this.importBeanNameGenerator);
        }
        this.reader.loadBeanDefinitions(configClasses);

    }


    private static class ImportAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

        private final BeanFactory beanFactory;

        public ImportAwareBeanPostProcessor(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
            // Inject the BeanFactory before AutowiredAnnotationBeanPostProcessor's
            // postProcessProperties method attempts to autowire other configuration beans.
            return pvs;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

    }
}
