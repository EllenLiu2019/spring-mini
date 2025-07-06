package com.minis.beans.factory.support;

import com.minis.beans.BeanFactory;
import com.minis.beans.BeansException;
import com.minis.beans.factory.config.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private final List<String> beanDefinitionNames = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    @Override
    public Object getBean(String beanName) throws BeansException, ReflectiveOperationException {
        Object singleton = this.getSingleton(beanName);
        if (singleton == null) {
            singleton = this.earlySingletonObjects.get(beanName);
            if (singleton == null) {
                BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
                singleton = creatBean(beanDefinition);
                this.registerBean(beanName, singleton);
                // TODO: 预留 bean postprocessor 位置
                //   step 1: postProcessBeforeInitialization
                applyBeanPostProcessorBeforeInitialization(singleton, beanName);
                //   step 2: afterPropertiesSet
                // TODO:  step 3: init-method
                /*if (beanDefinition.getInitMethodName() != null && !beanDefinition.equals("")) {
                    invokeInitMethod(beanDefinition, singleton);
                }*/
                // TODO:  step 4: postProcessAfterInitialization
                //applyBeanPostProcessorAfterInitialization(singleton, beanName);
            }
        }
        return singleton;
    }

    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                getBean(beanName);
            } catch (ReflectiveOperationException | BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Object creatBean(BeanDefinition beanDefinition) {
        Class<?> clz;
        Object obj;
        try {
            obj = doCreateBean(beanDefinition);
            this.earlySingletonObjects.put(beanDefinition.getId(), obj);

            clz = Class.forName(beanDefinition.getClassName());

            populateBean(beanDefinition, clz, obj);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    //TODO: handle constructor arguments
    private Object doCreateBean(BeanDefinition bd) throws ReflectiveOperationException {
        Class<?> clz = null;
        Object obj;
        Constructor<?> con;
        clz = Class.forName(bd.getClassName());
        ConstructorArgumentValues conArgValues = bd.getConstructorArgumentValues();
        if (!conArgValues.isEmpty()) {
            Class[] paramTypes = new Class[conArgValues.getArgumentCount()];
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
                } else {
                    paramTypes[i] = String.class;
                    paramValues[i] = conArgValue.getValue();
                }
            }
            con = clz.getConstructor(paramTypes);
            obj = con.newInstance(paramValues);
        } else {
            obj = clz.getConstructor().newInstance();
        }

        System.out.println(bd.getId() + " bean created. " + bd.getClassName() + " : " + obj);

        return obj;
    }

    //TODO: handle property values
    private void populateBean(BeanDefinition beanDefinition, Class<?> clz, Object obj) {
        handleProperties(beanDefinition, clz, obj);
    }

    private void handleProperties(BeanDefinition beanDefinition, Class<?> clz, Object obj) {
        System.out.println("handle properties for bean : " + beanDefinition.getId());

        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        if (!propertyValues.isEmpty()) {
            for (int i = 0; i < propertyValues.size(); i++) {
                PropertyValue pv = propertyValues.getPropertyValueList().get(i);

                boolean isRef = pv.getIsRef();
                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues = new Object[1];
                String pType = pv.getType();

                try {
                    if (!isRef) {
                        if ("String".equals(pType) || "java.lang.String".equals(pType)) {
                            paramTypes[0] = String.class;
                        } else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
                            paramTypes[0] = Integer.class;
                        } else if ("int".equals(pType)) {
                            paramTypes[0] = int.class;
                        } else {
                            paramTypes[0] = String.class;
                        }
                        paramValues[0] = pv.getValue();
                    } else {
                        paramTypes[0] = Class.forName(pType);
                        paramValues[0] = getBean((String) pv.getValue());
                    }

                    String methodName = "set" + pv.getName().substring(0, 1).toUpperCase() + pv.getName().substring(1);
                    Method method = clz.getMethod(methodName, paramTypes);

                    method.invoke(obj, paramValues);
                } catch (ReflectiveOperationException | BeansException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // TODO: why need this method? BeanFactory holds BeanDefinition,
    //   SingletonBeanRegistry holds singleton object
    @Override
    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
    }

    // TODO: why need this method? BeanFactory holds BeanDefinition,
    //  SingletonBeanRegistry holds singleton object
    @Override
    public boolean containsBean(String beanName) {
        return containsSingleton(beanName);
    }

    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String beanName) {
        return this.beanDefinitionMap.get(beanName).getBeanClass();
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);
    }

    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }
    public abstract Object applyBeanPostProcessorBeforeInitialization(Object existingBean, String beanName) throws BeansException, ReflectiveOperationException;
    //public abstract Object applyBeanPostProcessorAfterInitialization(Object existingBean, String beanName) throws BeansException;

}
