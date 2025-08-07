package com.minis.core.convert.support;

import com.minis.core.convert.converter.Converter;
import com.minis.utils.StringUtils;

import java.util.TimeZone;

class StringToTimeZoneConverter implements Converter<String, TimeZone> {
    @Override
    public TimeZone convert(String source) {
        if (StringUtils.hasText(source)) {
            source = source.trim();
        }

        return StringUtils.parseTimeZoneString(source);
    }
}
