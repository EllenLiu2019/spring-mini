package com.minis.beans.factory.config;

import com.minis.beans.PropertyValues;
import com.minis.beans.factory.support.ConfigurableBeanFactory;
import lombok.Getter;
import lombok.Setter;

public class BeanDefinition {

    static String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
    static String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;
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
}
