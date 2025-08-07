package com.minis.core.convert;

public interface ConversionService {

    boolean canConvert(TypeDescriptor sourceTypeDesc, TypeDescriptor typeDescriptor);

    Object convert(Object newValue, TypeDescriptor sourceTypeDesc, TypeDescriptor typeDescriptor);
}
