package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.FactoryBean;
import com.minis.beans.factory.config.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory, BeanDefinitionRegistry {
    private static final Logger LOGGER = LogManager.getLogger(AbstractBeanFactory.class);
    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    protected final List<String> beanDefinitionNames = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    @Override
    public Object getBean(String beanName) throws BeansException, ReflectiveOperationException {
        Object singleton = doGetBean(beanName);
        if (singleton instanceof FactoryBean) {
            singleton = this.getObjectForBeanInstance(singleton, beanName);
        }
        return singleton;
    }

    private Object getObjectForBeanInstance(Object singleton, String beanName) {
        Object object = getCachedObjectForFactoryBean(beanName);
        if (object == null) {
            FactoryBean<?> factory = (FactoryBean<?>) singleton;
            object = getObjectFromFactoryBean(factory, beanName);

        }
        return object;
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

    protected Object creatBean(String beanName, BeanDefinition beanDefinition) {
        Class<?> clz;
        Object obj;
        try {
            obj = doCreateBean(beanDefinition);

            this.earlySingletonObjects.put(beanName, obj);

            String beanClassName = beanDefinition.getBeanClassName() != null ? beanDefinition.getBeanClassName() : beanDefinition.getId();
            clz = Class.forName(beanClassName);

            populateBean(beanDefinition, clz, obj);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    //TODO: handle constructor arguments
    private Object doCreateBean(BeanDefinition bd) throws ReflectiveOperationException {
        String beanClassName = bd.getBeanClassName() != null ? bd.getBeanClassName() : bd.getClassName();
        LOGGER.debug("creating Bean for: id = {}, classname = {}", bd.getId(), beanClassName);
        Class<?> clz;
        Object obj;
        Constructor<?> con;
        clz = Class.forName(beanClassName);
        ConstructorArgumentValues conArgValues = bd.getConstructorArgumentValues();
        if (conArgValues != null && !conArgValues.isEmpty()) {
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
                } else if ("String[]".equals(conArgValue.getType())) {
                    paramTypes[i] = String[].class;
                    paramValues[i] = conArgValue.getValue();
                } else {
                    paramTypes[i] = String.class;
                    paramValues[i] = conArgValue.getValue();
                }
            }
            con = clz.getDeclaredConstructor(paramTypes);
            con.setAccessible(true);
            obj = con.newInstance(paramValues);
        } else {
            obj = createBeanInstance(beanClassName, clz, bd);
        }

        LOGGER.debug("bean created: id = {}, className = {}, obj = {}", bd.getId(), beanClassName, obj);

        return obj;
    }

    //TODO: handle property values
    private void populateBean(BeanDefinition beanDefinition, Class<?> clz, Object obj) {
        handleProperties(beanDefinition, clz, obj);
    }

    private void handleProperties(BeanDefinition beanDefinition, Class<?> clz, Object obj) {
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        if (propertyValues != null && !propertyValues.isEmpty()) {
            LOGGER.debug("populating Bean : id = {}, className = {}", beanDefinition.getId(), beanDefinition.getClassName());

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
                    Method method = clz.getMethod(methodName, paramTypes);

                    method.invoke(obj, paramValues);
                } catch (ReflectiveOperationException | BeansException e) {
                    throw new RuntimeException(e);
                }

                LOGGER.debug("Bean populated : id = {}, className = {}", beanDefinition.getId(), beanDefinition.getClassName());
            }
        }
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
        try {
            return Class.forName(this.beanDefinitionMap.get(beanName).getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    protected abstract Object doGetBean(String beanName) throws ReflectiveOperationException, BeansException;

    abstract Object createBeanInstance(String beanName, Class<?> clz, BeanDefinition bd) throws ReflectiveOperationException;

}
