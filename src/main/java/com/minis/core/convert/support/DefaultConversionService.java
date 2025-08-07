package com.minis.core.convert.support;

import com.minis.core.convert.ConversionService;
import com.minis.core.convert.converter.ConverterRegistry;
import com.minis.core.convert.converter.StringToLocalDateConverter;

import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

public class DefaultConversionService extends GenericConversionService {

    private static volatile DefaultConversionService sharedInstance;

    public DefaultConversionService() {
        addDefaultConverters(this);
    }

    public static ConversionService getSharedInstance() {
        DefaultConversionService cs = sharedInstance;
        if (cs == null) {
            synchronized (DefaultConversionService.class) {
                cs = sharedInstance;
                if (cs == null) {
                    cs = new DefaultConversionService();
                    sharedInstance = cs;
                }
            }
        }
        return cs;
    }

    public static void addDefaultConverters(ConverterRegistry converterRegistry) {
        addScalarConverters(converterRegistry);

        converterRegistry.addConverter(new StringToTimeZoneConverter());
        converterRegistry.addConverter(new ZoneIdToTimeZoneConverter());
        converterRegistry.addConverter(new StringToLocalDateConverter());
    }

    private static void addScalarConverters(ConverterRegistry converterRegistry) {
        converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
        converterRegistry.addConverter(Number.class, String.class, new ObjectToStringConverter());
        converterRegistry.addConverter(Character.class, String.class, new ObjectToStringConverter());
    }



}
