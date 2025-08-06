package com.minis.beans.factory.support;

import com.minis.beans.*;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.config.*;
import com.minis.utils.ClassUtils;
import com.minis.utils.ReflectionUtils;
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
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
        implements AutowireCapableBeanFactory, ListableBeanFactory {
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
    private boolean allowCircularReferences;

    private BeanPostProcessorCache beanPostProcessorCache;

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
        this.beanPostProcessorCache = null;
    }

    protected Object doGetBean(String beanName) throws BeansException {
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
            }
        }
        return singleton;
    }

    protected Object creatBean(String beanName, BeanDefinition beanDefinition) {
        Object obj;
        try {
            obj = doCreateBean(beanDefinition);

            // Allow post-processors to modify the merged bean definition.
            synchronized (beanDefinition.postProcessingLock) {
                if (!beanDefinition.postProcessed) {
                    try {
                        applyMergedBeanDefinitionPostProcessors(beanDefinition, beanName);
                    } catch (Throwable ex) {
                        throw new BeansException(beanName + ": Post-processing of merged bean definition failed: " + ex);
                    }
                    beanDefinition.markAsPostProcessed();
                }
            }

            this.earlySingletonObjects.put(beanName, obj);

            populateBean(beanDefinition, obj);

            obj = initializeBean(beanName, obj, beanDefinition);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    //TODO: handle constructor arguments
    private Object doCreateBean(BeanDefinition bd) throws ReflectiveOperationException {
        String beanClassName = bd.getBeanClassName() != null ? bd.getBeanClassName() : bd.getClassName();
        log.debug("creating Bean for: id = {}, classname = {}", bd.getId(), beanClassName);
        Class<?> clz;
        ConstructorArgumentValues conArgValues = bd.getConstructorArgumentValues();
        if (conArgValues != null && !conArgValues.isEmpty()) {
            clz = Class.forName(beanClassName);
            return resolveByConstructorArgs(clz, conArgValues);
        } else if (bd.getFactoryMethodName() != null) {
            return instantiateUsingFactoryMethod(beanClassName, bd);
        } else {
            clz = Class.forName(beanClassName);
            return createBeanInstance(beanClassName, clz, bd);
        }
    }

    private Object instantiateUsingFactoryMethod(String beanClassName, BeanDefinition bd) {
        BeanWrapperImpl bw = new BeanWrapperImpl();
        this.initBeanWrapper(bw);

        List<Method> candidates = new ArrayList<>();
        String factoryBeanName = bd.getFactoryBeanName();
        try {
            Object factoryBean = getBean(factoryBeanName);
            if (factoryBean != null) {
                Class<?> factoryClass = factoryBean.getClass();
                Method[] rawCandidates = factoryClass.getMethods();
                for (Method candidate : rawCandidates) {
                    if (bd.isFactoryMethod(candidate)) {
                        candidates.add(candidate);
                    }
                }
                Method uniqueCandidate = candidates.get(0);
                ReflectionUtils.makeAccessible(uniqueCandidate);
                return uniqueCandidate.invoke(factoryBean);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        throw new BeansException("cannot instantiate using factory method for bean " + beanClassName);
    }

    private Object resolveByConstructorArgs(Class<?> clz, ConstructorArgumentValues conArgValues) {
        Object obj;
        Constructor<?> con;
        Class<?>[] paramTypes = new Class[conArgValues.getArgumentCount()];
        Object[] paramValues = new Object[conArgValues.getArgumentCount()];
        for (int i = 0; i < conArgValues.getArgumentCount(); i++) {
            ConstructorArgumentValue conArgValue = conArgValues.getIndexedArgumentValue(i);
            if ("String".equals(conArgValue.getType()) || "java.lang.String".equals(conArgValue.getType())) {
                paramTypes[i] = String.class;
                paramValues[i] = conArgValue.getValue();
            } else if ("Integer".equals(conArgValue.getType()) || "java.lang.Integer".equals(conArgValue.getType())) {
                paramTypes[i] = Integer.class;
                paramValues[i] = Integer.valueOf((String) conArgValue.getValue());
            } else if ("int".equals(conArgValue.getType())) {
                paramTypes[i] = int.class;
                paramValues[i] = Integer.valueOf((String) conArgValue.getValue());
            } else if ("String[]".equals(conArgValue.getType())) {
                paramTypes[i] = String[].class;
                paramValues[i] = conArgValue.getValue();
            } else {
                paramTypes[i] = String.class;
                paramValues[i] = conArgValue.getValue();
            }
        }
        try {
            con = clz.getDeclaredConstructor(paramTypes);
            con.setAccessible(true);
            obj = con.newInstance(paramValues);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    protected void applyMergedBeanDefinitionPostProcessors(BeanDefinition mbd, String beanName) {
        for (MergedBeanDefinitionPostProcessor processor : getBeanPostProcessorCache().mergedDefinition) {
            processor.postProcessMergedBeanDefinition(mbd, beanName);
        }
    }

    //TODO: handle property values
    private void populateBean(BeanDefinition beanDefinition, Object obj) {
        String beanClassName = beanDefinition.getBeanClassName() != null ? beanDefinition.getBeanClassName() : beanDefinition.getId();

        PropertyValues pvs = beanDefinition.getPropertyValues();

        for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
            PropertyValues pvsToUse = bp.postProcessProperties(pvs, obj, beanClassName);
            pvs = pvsToUse;
        }

        handleProperties(beanDefinition, obj, beanClassName);
    }

    BeanPostProcessorCache getBeanPostProcessorCache() {
        synchronized (this.beanPostProcessors) {
            BeanPostProcessorCache bppCache = this.beanPostProcessorCache;
            if (bppCache == null) {
                bppCache = new BeanPostProcessorCache();
                for (BeanPostProcessor bpp : this.beanPostProcessors) {
                    if (bpp instanceof InstantiationAwareBeanPostProcessor instantiationAwareBpp) {
                        bppCache.instantiationAware.add(instantiationAwareBpp);
                    }
                    if (bpp instanceof MergedBeanDefinitionPostProcessor mergedBeanDefBpp) {
                        bppCache.mergedDefinition.add(mergedBeanDefBpp);
                    }
                }
                this.beanPostProcessorCache = bppCache;
            }
            return bppCache;
        }
    }

    private void handleProperties(BeanDefinition beanDefinition, Object obj, String beanClassName) {
        PropertyValues propertyValues = beanDefinition.getPropertyValues();

        if (propertyValues != null && !propertyValues.isEmpty()) {

            log.debug("populating Bean : id = {}, className = {}", beanDefinition.getId(), beanClassName);

            for (int i = 0; i < propertyValues.size(); i++) {
                PropertyValue pv = propertyValues.getPropertyValueList().get(i);

                boolean isRef = pv.getIsRef();
                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues = new Object[1];
                String pType = pv.getType();
                Object pValue = pv.getValue();

                try {
                    if (!isRef) {
                        if ("String".equals(pType) || "java.lang.String".equals(pType)) {
                            paramTypes[0] = String.class;
                            paramValues[0] = pValue;
                        } else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
                            paramTypes[0] = Integer.class;
                            paramValues[0] = Integer.valueOf((String) pValue);
                        } else if ("int".equals(pType)) {
                            paramTypes[0] = int.class;
                            paramValues[0] = Integer.valueOf((String) pValue).intValue();
                        } else {
                            paramTypes[0] = String.class;
                            paramValues[0] = pValue;
                        }
                    } else {
                        paramTypes[0] = Class.forName(pType);
                        paramValues[0] = getBean((String) pv.getValue());
                    }

                    String methodName = "set" + pv.getName().substring(0, 1).toUpperCase() + pv.getName().substring(1);
                    Method method = Class.forName(beanClassName).getMethod(methodName, paramTypes);

                    method.invoke(obj, paramValues);
                } catch (ReflectiveOperationException | BeansException e) {
                    throw new RuntimeException(e);
                }

                log.debug("Bean populated : id = {}, className = {}", beanDefinition.getId(), beanDefinition.getClassName());
            }
        }
    }

    private Object initializeBean(String beanName, Object bean, BeanDefinition bd) throws ReflectiveOperationException {
        invokeAwareMethods(bean);

        Object singleton = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        if (bd.getInitMethodName() != null) {
            invokeInitMethod(bd, singleton);
        }

        singleton = applyBeanPostProcessorsAfterInitialization(singleton, beanName);

        return singleton;
    }

    private void invokeAwareMethods(Object bean) {
        if (bean instanceof BeanFactoryAware beanFactoryAware) {
            beanFactoryAware.setBeanFactory(this);
        }
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
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException, ReflectiveOperationException {
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
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
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
            Object beanInstance = clazz.getConstructor().newInstance();
            BeanWrapper bw = new BeanWrapperImpl(beanInstance);
            initBeanWrapper(bw);
            return beanInstance;
        } catch (NoSuchMethodException e) {
            log.info("no default constructor, autowired try");
            return autowireConstructor(clazz);
        }
    }

    private Object autowireConstructor(Class<?> clazz) throws ReflectiveOperationException {

        BeanWrapperImpl bw = new BeanWrapperImpl();
        this.initBeanWrapper(bw);

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

    static class BeanPostProcessorCache {

        final List<InstantiationAwareBeanPostProcessor> instantiationAware = new ArrayList<>();
        final List<MergedBeanDefinitionPostProcessor> mergedDefinition = new ArrayList<>();
    }

}
