package com.minis.core.convert.converter;

import com.minis.core.convert.TypeDescriptor;

public interface ConditionalConverter {

    boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);
}
