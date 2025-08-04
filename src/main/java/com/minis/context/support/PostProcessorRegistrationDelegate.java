package com.minis.context.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;

import java.util.*;

final public class PostProcessorRegistrationDelegate {
    private PostProcessorRegistrationDelegate() {
    }

    public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) throws BeansException, ReflectiveOperationException {

        Set<String> processedBeans = new HashSet<>();
        // Invoke BeanDefinitionRegistryPostProcessors first, if any.
        if (beanFactory instanceof BeanDefinitionRegistry registry) {
            // Do not initialize FactoryBeans here: We need to leave all regular beans
            // uninitialized to let the bean factory post-processors apply to them!
            List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();
            List<BeanDefinitionRegistryPostProcessor> registryProcessors;
            String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class);
            for (String ppName : postProcessorNames) {
                currentRegistryProcessors.add((BeanDefinitionRegistryPostProcessor) beanFactory.getBean(ppName));
                processedBeans.add(ppName);
            }
            registryProcessors = new ArrayList<>(currentRegistryProcessors);

            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
            // invoke the postProcessBeanFactory callback of all registry handled so far.
            invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
        }

        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class);

        List<BeanFactoryPostProcessor> postProcessors = new ArrayList<>();
        for (String ppName : postProcessorNames) {
            if (processedBeans.contains(ppName)) {
                // skip - already processed in first phase above
            } else {
                postProcessors.add((BeanFactoryPostProcessor) beanFactory.getBean(ppName));
            }
        }

        invokeBeanFactoryPostProcessors(postProcessors, beanFactory);

    }

    private static void invokeBeanDefinitionRegistryPostProcessors(Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors,
                                                                   BeanDefinitionRegistry registry) throws BeansException {
        for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessBeanDefinitionRegistry(registry);
        }
    }

    private static void invokeBeanFactoryPostProcessors(Collection<? extends BeanFactoryPostProcessor> postProcessors,
                                                        ConfigurableListableBeanFactory beanFactory) {
        for (BeanFactoryPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    public static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) throws ReflectiveOperationException, BeansException {
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class);
        for (String postProcessorName : postProcessorNames) {
            beanFactory.addBeanPostProcessor((BeanPostProcessor) beanFactory.getBean(postProcessorName));
        }
    }
}
