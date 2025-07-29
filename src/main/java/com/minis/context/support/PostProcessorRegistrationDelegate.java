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

        // Invoke BeanDefinitionRegistryPostProcessors first, if any.
        if (beanFactory instanceof BeanDefinitionRegistry registry) {
            // Do not initialize FactoryBeans here: We need to leave all regular beans
            // uninitialized to let the bean factory post-processors apply to them!
            List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();
            List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
            String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class);
            for (String ppName : postProcessorNames) {
                currentRegistryProcessors.add((BeanDefinitionRegistryPostProcessor) beanFactory.getBean(ppName));
            }
            registryProcessors = new ArrayList<>(currentRegistryProcessors);

            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
            invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
        }

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
        for(String postProcessorName : postProcessorNames) {
            beanFactory.addBeanPostProcessor((BeanPostProcessor) beanFactory.getBean(postProcessorName));
        }
    }
}
