package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.utils.ClassUtils;
import com.minis.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: 实现 postProcessor, 继承自 AbstractBeanFactory
//  成员变量 List<BeanPostProcessor> beanPostProcessors 用于存储 BeanPostProcessor
@Slf4j
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory, ListableBeanFactory {
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
    private boolean allowCircularReferences;

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    protected Object doGetBean(String beanName) throws ReflectiveOperationException, BeansException {
        Object singleton = getSingleton(beanName);
        if (singleton == null) {
            singleton = this.earlySingletonObjects.get(beanName);
            if (singleton == null) {
                BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
                if (beanDefinition == null || beanDefinition.equals(StringUtils.EMPTY)) {
                    log.debug("bean definition not exit for {}", beanName);
                    return null;
                }
                log.debug("NOT early exposed yet, creating singleton bean '" + beanName + "'");

                singleton = creatBean(beanName, beanDefinition);

                this.registerSingleton(beanName, singleton);

                if (singleton instanceof BeanFactoryAware) {
                    ((BeanFactoryAware) singleton).setBeanFactory(this);
                }

                // TODO: postProcess Before Initialization
                singleton = applyBeanPostProcessorBeforeInitialization(singleton, beanName);

                // TODO: init-method
                if (beanDefinition.getInitMethodName() != null) {
                    invokeInitMethod(beanDefinition, singleton);
                }
                // TODO: postProcess After Initialization
                applyBeanPostProcessorAfterInitialization(singleton, beanName);

                this.removeSingleton(beanName);
                this.registerSingleton(beanName, singleton);
            }
        }
        return singleton;
    }

    void invokeInitMethod(BeanDefinition beanDef, Object singleton) {
        Class<?> clazz = singleton.getClass();
        String initMethodName = beanDef.getInitMethodName();
        if (initMethodName != null && !initMethodName.isEmpty()) {
            Method initMethod = ClassUtils.getMethod(clazz, initMethodName);
            try {
                initMethod.invoke(singleton);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }

    @Override
    public Object applyBeanPostProcessorBeforeInitialization(Object existingBean, String beanName) throws BeansException, ReflectiveOperationException {
        Object result = existingBean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            beanPostProcessor.setBeanFactory(this);
            result = beanPostProcessor.postProcessBeforeInitialization(result, beanName);
            if (result == null) {
                return null;
            }
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            beanPostProcessor.setBeanFactory(this);
            result = beanPostProcessor.postProcessAfterInitialization(result, beanName);
            if (result == null) {
                return null;
            }
        }
        return result;
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }

    @Override
    public Object createBeanInstance(String beanName, Class<?> clazz, BeanDefinition bd) throws ReflectiveOperationException {
        try {
            return clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            log.info("no default constructor, autowired try");
            return autowireConstructor(clazz);
        }
    }

    private Object autowireConstructor(Class<?> clazz) throws ReflectiveOperationException {
        Object instance;
        Constructor<?>[] ctors = clazz.getDeclaredConstructors();
        for (Constructor<?> ctor : ctors) {
            Class<?>[] parameterTypes = ctor.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            try {
                for (int i = 0; i < parameterTypes.length; i++) {
                    String[] beanNames = getBeanNamesForType(parameterTypes[i]);
                    Object arg = getBean(beanNames[0]);
                    args[i] = arg;
                }
                instance = ctor.newInstance(args);
            } catch (ReflectiveOperationException | BeansException e) {
                throw new RuntimeException(e);
            }
            return instance;
        }
        throw new ReflectiveOperationException("No matching constructor found for bean '" + clazz + "'");
    }
}
