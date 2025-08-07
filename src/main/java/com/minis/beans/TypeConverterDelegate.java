package com.minis.beans;


import com.minis.core.convert.ConversionFailedException;
import com.minis.core.convert.ConversionService;
import com.minis.core.convert.TypeDescriptor;

import java.beans.PropertyEditor;

class TypeConverterDelegate {

    private final PropertyEditorRegistrySupport propertyEditorRegistry;

    private final Object targetObject;

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
        this(propertyEditorRegistry, null);
    }

    /**
     * Create a new TypeConverterDelegate for the given editor registry and bean instance.
     * @param propertyEditorRegistry the editor registry to use
     * @param targetObject the target object to work on (as context that can be passed to editors)
     */
    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry, Object targetObject) {
        this.propertyEditorRegistry = propertyEditorRegistry;
        this.targetObject = targetObject;
    }

    public <T> T convertIfNecessary(String propertyName, Object oldValue, Object newValue,
                                    Class<T> requiredType, TypeDescriptor typeDescriptor) {

        PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);

        ConversionFailedException conversionAttemptEx = null;

        ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
        if (editor == null && conversionService != null && newValue != null && typeDescriptor != null) {
            TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
            if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
                try {
                    return (T) conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
                }
                catch (ConversionFailedException ex) {
                    // fallback to default conversion logic below
                    conversionAttemptEx = ex;
                }
            }
        }
        return null;
    }
}
