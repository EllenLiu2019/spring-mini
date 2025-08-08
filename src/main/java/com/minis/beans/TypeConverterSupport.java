package com.minis.beans;

import com.minis.core.MethodParameter;
import com.minis.core.convert.TypeDescriptor;


public class TypeConverterSupport extends PropertyEditorRegistrySupport implements TypeConverter {

    TypeConverterDelegate typeConverterDelegate;

    @Override
    public <T> T convertIfNecessary(Object value, Class<T> requiredType,
                                    TypeDescriptor typeDescriptor) throws BeansException {
        return this.convertIfNecessary(
                null, value, requiredType, typeDescriptor);
    }

    public <T> T convertIfNecessary(Object value, Class<T> requiredType,
                                    MethodParameter methodParam) throws BeansException {

        return this.convertIfNecessary(methodParam.getParameterName(), value, requiredType, new TypeDescriptor(methodParam));
    }

    private <T> T convertIfNecessary(String propertyName, Object value,
                                     Class<T> requiredType, TypeDescriptor typeDescriptor) throws BeansException {

        return this.typeConverterDelegate.convertIfNecessary(
                propertyName, value, requiredType, typeDescriptor);

    }
}
