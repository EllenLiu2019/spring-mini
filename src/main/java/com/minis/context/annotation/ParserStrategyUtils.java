package com.minis.context.annotation;

import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.support.BeanDefinitionRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

abstract class ParserStrategyUtils {

    @SuppressWarnings("unchecked")
    static <T> T instantiateClass(Class<?> clazz, BeanDefinitionRegistry registry) {
        if (clazz.isInterface()) {
            throw new RuntimeException("Specified class is an interface");
        }
        T instance = (T) createInstance(clazz);
        ParserStrategyUtils.invokeAwareMethods(instance, registry);
        return instance;
    }

    private static Object createInstance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> void invokeAwareMethods(T parserStrategyBean, BeanDefinitionRegistry registry) {
        if (parserStrategyBean instanceof BeanFactoryAware beanFactoryAware && registry instanceof BeanFactory beanFactory) {
            beanFactoryAware.setBeanFactory(beanFactory);
        }
    }


}
