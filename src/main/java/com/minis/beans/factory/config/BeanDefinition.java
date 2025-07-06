package com.minis.beans.factory.config;

public class BeanDefinition {

    static String SCOPE_SINGLETON = "singleton";
    static String SCOPE_PROTOTYPE = "prototype";
    private String scope = SCOPE_SINGLETON;

    public boolean isLazyInit() {
        return lazyInit;
    }

    private boolean lazyInit = false;
    private String[] dependsOn;
    private ArgumentValues constructorArgumentValues;
    private PropertyValues propertyValues;
    private String initMethodName;
    private volatile Object beanClass;
    private String id;
    private String className;

    public BeanDefinition(String id, String className) {
        this.id = id;
        this.className = className;
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

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(this.scope);
    }

    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(this.scope);
    }

    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }
    public Class<?> getBeanClass() {
        return (Class<?>) this.beanClass;
    }

    public void setPropertyValues(PropertyValues pvs) {
        this.propertyValues = pvs;
    }

    public void setConstructorArgumentValues(ArgumentValues args) {
        this.constructorArgumentValues = args;
    }

    public ArgumentValues getConstructorArgumentValues() {
        return this.constructorArgumentValues;
    }

    public PropertyValues getPropertyValues() {
        return this.propertyValues;
    }
}
