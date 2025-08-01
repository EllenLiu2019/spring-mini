package com.minis.beans.factory.config;

import com.minis.beans.PropertyValues;
import com.minis.beans.factory.support.AutowireCapableBeanFactory;
import com.minis.beans.factory.support.ConfigurableBeanFactory;
import com.minis.core.io.Resource;
import com.minis.utils.ClassUtils;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class BeanDefinition {

    static String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
    static String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

    public static final int AUTOWIRE_NO = AutowireCapableBeanFactory.AUTOWIRE_NO;

    public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

    public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

    public static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

    private String scope = SCOPE_SINGLETON;

    private boolean lazyInit = false;
    private String[] dependsOn;
    private ConstructorArgumentValues constructorArgumentValues;
    private PropertyValues propertyValues;

    @Getter
    @Setter
    private String initMethodName;
    private String id;
    private String className;

    private volatile Object beanClass;
    private String factoryMethodName;
    private boolean primary;

    private String factoryBeanName;

    private int autowireMode = AUTOWIRE_NO;

    private final Map<String, Object> attributes = new LinkedHashMap<>();

    private Resource resource;

    public BeanDefinition(String id, String className) {
        this.id = id;
        this.className = className;
    }

    public BeanDefinition() {
    }

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public void setBeanClass(Object beanClass) {
        this.beanClass = beanClass;
    }

    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }

    public Class<?> getBeanClass() throws IllegalStateException {
        Object beanClassObject = this.beanClass;  // defensive access to volatile beanClass field
        if (beanClassObject == null) {
            throw new IllegalStateException("No bean class specified on bean definition");
        }
        if (!(beanClassObject instanceof Class<?> clazz)) {
            throw new IllegalStateException(
                    "Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
        }
        return clazz;
    }

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClassName(String beanClassName) {
        this.beanClass = beanClassName;
    }

    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(this.scope);
    }

    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(this.scope);
    }

    public void setPropertyValues(PropertyValues pvs) {
        this.propertyValues = pvs;
    }

    public void setConstructorArgumentValues(ConstructorArgumentValues args) {
        this.constructorArgumentValues = args;
    }

    public ConstructorArgumentValues getConstructorArgumentValues() {
        return this.constructorArgumentValues;
    }

    public PropertyValues getPropertyValues() {
        return this.propertyValues;
    }

    public void setDependsOn(String[] refArray) {
        this.dependsOn = refArray;
    }

    public String getBeanClassName() {
        Object beanClassObject = this.beanClass;  // defensive access to volatile beanClass field
        return (beanClassObject instanceof Class<?> clazz ? clazz.getName() : (String) beanClassObject);
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClass = beanClassName;
    }

    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }

    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isPrimary() {
        return this.primary;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        if (value != null) {
            this.attributes.put(name, value);
        } else {
            removeAttribute(name);
        }
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    public Class<?> resolveBeanClass(ClassLoader classLoader) throws ClassNotFoundException {
        String className = getBeanClassName();
        if (className == null) {
            return null;
        }
        Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
        this.beanClass = resolvedClass;
        return resolvedClass;
    }

    public boolean isFactoryMethod(Method candidate) {
        return candidate.getName().equals(this.getFactoryMethodName());
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return this.resource;
    }
}
