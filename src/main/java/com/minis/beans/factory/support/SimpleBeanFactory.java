package com.minis.beans.factory.support;

import com.minis.beans.BeanFactory;
import com.minis.beans.BeansException;
import com.minis.beans.factory.config.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private List<String> beanDefinitionNames = new ArrayList<>();

    @Override
    public Object getBean(String beanName) throws BeansException, ReflectiveOperationException {
        Object singleton = this.singletons.get(beanName);
        if (singleton == null) {
            BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
            if (beanDefinition == null) {
                throw new BeansException("No bean named '" + beanName + "' is defined");
            }
            singleton = creatBean(beanDefinition);
            this.registerSingleton(beanName, singleton);
        }
        return singleton;
    }

    private Object creatBean(BeanDefinition beanDefinition) {
        Class<?> clz;
        Object obj;
        Constructor<?> con;
        try {
            clz = Class.forName(beanDefinition.getClassName());

            //TODO: handle constructor arguments
            ArgumentValues argumentValues = beanDefinition.getConstructorArgumentValues();
            if (!argumentValues.isEmpty()) {
                Class[] paramTypes = new Class[argumentValues.getArgumentCount()];
                Object[] paramValues = new Object[argumentValues.getArgumentCount()];
                for (int i = 0; i < argumentValues.getArgumentCount(); i++) {
                    ArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
                    if ("String".equals(argumentValue.getType()) || "java.lang.String".equals(argumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    } else if ("Integer".equals(argumentValue.getType()) || "java.lang.Integer".equals(argumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                    } else if ("int".equals(argumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                    } else {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                }
                con = clz.getConstructor(paramTypes);
                obj = con.newInstance(paramValues);
            } else {
                obj = clz.getConstructor().newInstance();
            }

            //TODO: handle property values
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            if (!propertyValues.isEmpty()) {
                for (int i = 0; i < propertyValues.size(); i++) {
                    PropertyValue pv = propertyValues.getPropertyValueList().get(i);

                    String pType = pv.getType();
                    Class<?>[] paramTypes = new Class<?>[1];
                    if ("String".equals(pType) || "java.lang.String".equals(pType)) {
                        paramTypes[0] = String.class;
                    } else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
                        paramTypes[0] = Integer.class;
                    } else if ("int".equals(pType)) {
                        paramTypes[0] = int.class;
                    } else {
                        paramTypes[0] = String.class;
                    }

                    Object[] paramValues = new Object[1];
                    paramValues[0] = pv.getValue();

                    String methodName = "set" + pv.getName().substring(0, 1).toUpperCase() + pv.getName().substring(1);
                    Method method = clz.getMethod(methodName, paramTypes);

                    method.invoke(obj, paramValues);
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return obj;
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
        // TODO: 注册单例，非懒加载则直接创建 bean 实例
        if (!beanDefinition.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException | ReflectiveOperationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
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


}
