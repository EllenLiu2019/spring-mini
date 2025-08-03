package com.minis.core.env;

import java.util.Map;

public abstract class AbstractEnvironment implements ConfigurableEnvironment {

    private final MutablePropertySources propertySources;

    private PropertyResolver propertyResolver;

    protected AbstractEnvironment(MutablePropertySources propertySources) {
        this.propertySources = propertySources;
        //this.propertyResolver = createPropertyResolver(propertySources);
        customizePropertySources(propertySources);
    }

    public AbstractEnvironment() {
        this(new MutablePropertySources());
    }

    /*protected ConfigurablePropertyResolver createPropertyResolver(MutablePropertySources propertySources) {
        return new PropertySourcesPropertyResolver(propertySources);
    }*/

    protected void customizePropertySources(MutablePropertySources propertySources) {
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map<String, Object> getSystemProperties() {
        return (Map) System.getProperties();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map<String, Object> getSystemEnvironment() {
        return (Map) System.getenv();
    }

    @Override
    public MutablePropertySources getPropertySources() {
        return this.propertySources;
    }

    //---------------------------------------------------------------------
    // Implementation of PropertyResolver interface
    //---------------------------------------------------------------------

    @Override
    public String getProperty(String key) {
        return this.propertyResolver.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return this.propertyResolver.getProperty(key, defaultValue);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return this.propertyResolver.getProperty(key, targetType);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return this.propertyResolver.getProperty(key, targetType, defaultValue);
    }
}
