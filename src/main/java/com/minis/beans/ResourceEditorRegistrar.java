package com.minis.beans;

import com.minis.core.env.PropertyResolver;


import java.beans.PropertyEditor;


public class ResourceEditorRegistrar implements PropertyEditorRegistrar {

    private final PropertyResolver propertyResolver;

    public ResourceEditorRegistrar(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        //doRegisterEditor(registry, Resource.class, null);
    }

    private void doRegisterEditor(PropertyEditorRegistry registry, Class<?> requiredType, PropertyEditor editor) {
        if (registry instanceof PropertyEditorRegistrySupport registrySupport) {
            registrySupport.overrideDefaultEditor(requiredType, editor);
        } else {
            registry.registerCustomEditor(requiredType, editor);
        }
    }
}
