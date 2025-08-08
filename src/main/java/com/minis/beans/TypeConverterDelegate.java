package com.minis.beans;


import com.minis.core.convert.ConversionFailedException;
import com.minis.core.convert.ConversionService;
import com.minis.core.convert.TypeDescriptor;
import com.minis.utils.ClassUtils;
import com.minis.utils.NumberUtils;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyEditor;
import java.util.Optional;

@Slf4j
class TypeConverterDelegate {

    private final PropertyEditorRegistrySupport propertyEditorRegistry;

    private final Object targetObject;

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
        this(propertyEditorRegistry, null);
    }

    /**
     * Create a new TypeConverterDelegate for the given editor registry and bean instance.
     *
     * @param propertyEditorRegistry the editor registry to use
     * @param targetObject           the target object to work on (as context that can be passed to editors)
     */
    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry, Object targetObject) {
        this.propertyEditorRegistry = propertyEditorRegistry;
        this.targetObject = targetObject;
    }

    public <T> T convertIfNecessary(String propertyName, Object newValue,
                                    Class<T> requiredType, TypeDescriptor typeDescriptor) {

        PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);

        ConversionFailedException conversionAttemptEx = null;

        ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
        if (editor == null && conversionService != null && newValue != null && typeDescriptor != null) {
            TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
            if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
                try {
                    return (T) conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
                } catch (ConversionFailedException ex) {
                    // fallback to default conversion logic below
                    conversionAttemptEx = ex;
                }
            }
        }

        Object convertedValue = newValue;

        if (editor != null || (requiredType != null && !ClassUtils.isAssignableValue(requiredType, convertedValue))) {
            if (editor == null) {
                editor = findDefaultEditor(requiredType);
            }
            convertedValue = doConvertValue(convertedValue, requiredType, editor);
        }

        boolean standardConversion = false;

        if (requiredType != null) {
            // Try to apply some standard type conversion rules if appropriate.
            if (convertedValue != null) {
                if (Object.class == requiredType) {
                    return (T) convertedValue;
                }
                if (String.class == requiredType && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                    return (T) convertedValue.toString();
                } else if (convertedValue instanceof Number num && Number.class.isAssignableFrom(requiredType)) {
                    convertedValue = NumberUtils.convertNumberToTargetClass(num, (Class<Number>) requiredType);
                    standardConversion = true;
                }
            } else {
                if (requiredType == Optional.class) {
                    convertedValue = Optional.empty();
                }
            }

            if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
                if (conversionAttemptEx != null) {
                    // Original exception from former ConversionService call above...
                    throw conversionAttemptEx;
                } else if (conversionService != null && typeDescriptor != null) {
                    // ConversionService not tried before, probably custom editor found
                    // but editor couldn't produce the required type...
                    TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
                    if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
                        return (T) conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
                    }
                }

                // Definitely doesn't match: throw IllegalArgumentException/IllegalStateException
                StringBuilder msg = new StringBuilder();
                msg.append("Cannot convert value of type '").append(ClassUtils.getDescriptiveType(newValue));
                msg.append("' to required type '").append(ClassUtils.getQualifiedName(requiredType)).append('\'');
                if (propertyName != null) {
                    msg.append(" for property '").append(propertyName).append('\'');
                }
                if (editor != null) {
                    msg.append(": PropertyEditor [").append(editor.getClass().getName()).append(
                            "] returned inappropriate value of type '").append(
                            ClassUtils.getDescriptiveType(convertedValue)).append('\'');
                    throw new IllegalArgumentException(msg.toString());
                } else {
                    msg.append(": no matching editors or conversion strategy found");
                    throw new IllegalStateException(msg.toString());
                }
            }
        }

        if (conversionAttemptEx != null) {
            if (editor == null && !standardConversion && requiredType != null && Object.class != requiredType) {
                throw conversionAttemptEx;
            }
            log.debug("Original ConversionService attempt failed - ignored since " +
                    "PropertyEditor based conversion eventually succeeded", conversionAttemptEx);
        }


        return (T) convertedValue;
    }

    private <T> Object doConvertValue(Object convertedValue, Class<T> requiredType, PropertyEditor editor) {
        Object returnValue = convertedValue;
        if (convertedValue instanceof String newTextValue) {
            if (editor != null) {
                // Use PropertyEditor's setAsText in case of a String value.
                log.trace("Converting String to [" + requiredType + "] using property editor [" + editor + "]");
                editor.setAsText(newTextValue);
                return editor.getValue();
            } else if (String.class == requiredType) {
                returnValue = convertedValue;
            }
        }
        return returnValue;
    }

    private PropertyEditor findDefaultEditor(Class<?> requiredType) {
        PropertyEditor editor = null;
        if (requiredType != null) {
            // No custom editor -> check BeanWrapperImpl's default editors.
            editor = this.propertyEditorRegistry.getDefaultEditor(requiredType);
        }
        return editor;
    }
}
