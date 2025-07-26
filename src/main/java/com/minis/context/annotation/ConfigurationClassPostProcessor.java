package com.minis.context.annotation;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanDefinitionHolder;
import com.minis.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.minis.beans.factory.support.BeanNameGenerator;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;

import java.util.*;

/**
 * TODO: Registered by AnnotationConfigUtils.class
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {

    public static final AnnotationBeanNameGenerator IMPORT_BEAN_NAME_GENERATOR =
            FullyQualifiedAnnotationBeanNameGenerator.INSTANCE;

    private static final String IMPORT_REGISTRY_BEAN_NAME =
            ConfigurationClassPostProcessor.class.getName() + ".importRegistry";

    private final Set<Integer> factoriesPostProcessed = new HashSet<>();

    private final Set<Integer> registriesPostProcessed = new HashSet<>();


    private ConfigurationClassBeanDefinitionReader reader;
    private BeanNameGenerator importBeanNameGenerator = IMPORT_BEAN_NAME_GENERATOR;


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

    private void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
        // TODO: any candidates are then enhanced by a ConfigurationClassEnhancer.
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
        parser.parse(candidates); // 主类 App

        Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());

        if (this.reader == null) {
            this.reader = new ConfigurationClassBeanDefinitionReader(registry, this.importBeanNameGenerator);
        }
        this.reader.loadBeanDefinitions(configClasses);

    }

    @Override
    /**
     * Prepare the Configuration classes for servicing bean requests at runtime by replacing them with CGLIB-enhanced subclasses.
     */
    public void postProcessBeanFactory(BeanFactory beanFactory) {

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
            /*if (bean instanceof EnhancedConfiguration enhancedConfiguration) {
                enhancedConfiguration.setBeanFactory(this.beanFactory);
            }*/
            return pvs;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException, ReflectiveOperationException {
            return bean;
        }

    }
}
