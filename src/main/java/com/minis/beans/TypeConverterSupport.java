package com.minis.beans;

import com.minis.core.convert.TypeDescriptor;


public class TypeConverterSupport extends PropertyEditorRegistrySupport implements TypeConverter {

    TypeConverterDelegate typeConverterDelegate;

    @Override
    public <T> T convertIfNecessary(Object value, Class<T> requiredType,
                                    TypeDescriptor typeDescriptor) throws BeansException {
        return this.typeConverterDelegate.convertIfNecessary(
                null, null, value, requiredType, typeDescriptor);
    }
}
