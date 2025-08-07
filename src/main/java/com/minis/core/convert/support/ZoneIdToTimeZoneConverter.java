package com.minis.core.convert.support;

import com.minis.core.convert.converter.Converter;

import java.time.ZoneId;
import java.util.TimeZone;

public class ZoneIdToTimeZoneConverter implements Converter<ZoneId, TimeZone> {
    @Override
    public TimeZone convert(ZoneId source) {
        return TimeZone.getTimeZone(source);
    }
}
