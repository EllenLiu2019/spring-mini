package com.minis.beans;

import com.minis.core.MethodParameter;
import com.minis.core.convert.TypeDescriptor;

public interface TypeConverter {

    default <T> T convertIfNecessary(Object value, Class<T> requiredType,
                                     TypeDescriptor typeDescriptor) throws BeansException {
        throw new UnsupportedOperationException("TypeDescriptor resolution not supported");
    }
    <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam);
}
