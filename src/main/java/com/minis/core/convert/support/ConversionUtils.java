package com.minis.core.convert.support;

import com.minis.core.convert.ConversionFailedException;
import com.minis.core.convert.TypeDescriptor;
import com.minis.core.convert.converter.GenericConverter;

abstract class ConversionUtils {

    public static Object invokeConverter(GenericConverter converter, Object source,
                                         TypeDescriptor sourceType, TypeDescriptor targetType) {

        try {
            return converter.convert(source, sourceType, targetType);
        } catch (Throwable ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex);
        }
    }
}
