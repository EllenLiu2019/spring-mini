package com.minis.core.convert;

public class ConversionFailedException extends RuntimeException{
    private final TypeDescriptor sourceType;

    private final TypeDescriptor targetType;

    private final Object value;

    public ConversionFailedException(TypeDescriptor sourceType, TypeDescriptor targetType,
                                     Object value, Throwable cause) {

        super("Failed to convert from type [" + sourceType + "] to type [" + targetType +
                "] for value [" + value + "]", cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.value = value;
    }

}
