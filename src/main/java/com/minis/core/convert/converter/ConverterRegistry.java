package com.minis.core.convert.converter;

public interface ConverterRegistry {

    <S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<? super S, ? extends T> converter);

    void addConverter(GenericConverter converter);

    void addConverter(Converter<?, ?> converter);

    void addConverterFactory(ConverterFactory<?, ?> factory);
}
