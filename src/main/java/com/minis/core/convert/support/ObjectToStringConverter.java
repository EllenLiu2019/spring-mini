package com.minis.core.convert.support;

import com.minis.core.convert.converter.Converter;

public class ObjectToStringConverter implements Converter<Object, String> {
    @Override
    public String convert(Object source) {
        return source.toString();
    }
}
