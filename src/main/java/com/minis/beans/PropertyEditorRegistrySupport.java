package com.minis.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PropertyEditorRegistrySupport {
    private Map<Class<?>, PropertyEditor> defaultEditors;
    private Map<Class<?>, PropertyEditor> customEditors;

    public PropertyEditorRegistrySupport() {
        this.registerDefaultEditors();
    }

    protected void registerDefaultEditors() {
        this.defaultEditors = new HashMap<>(64);

        // Default instances of collection editors.
        this.defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
        this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
        this.defaultEditors.put(long.class, new CustomNumberEditor(Long.class, false));
        this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
        this.defaultEditors.put(float.class, new CustomNumberEditor(Float.class, false));
        this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
        this.defaultEditors.put(double.class, new CustomNumberEditor(Double.class, false));
        this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
        this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
        this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));

        this.defaultEditors.put(String.class, new StringEditor(String.class, true));
    }

    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        if (this.customEditors == null) {
            this.customEditors = new LinkedHashMap<>(16);
        }
        this.customEditors.put(requiredType, propertyEditor);
    }

    public PropertyEditor getDefaultEditor(Class<?> requiredType) {
        return this.defaultEditors.get(requiredType);
    }

    public PropertyEditor getCustomEditor(Class<?> requiredType) {
        if (requiredType == null || this.customEditors == null) {
            return null;
        }
        // Check directly registered editor for type.
        return this.customEditors.get(requiredType);
    }
}
