package com.minis.core.env;

import com.minis.core.convert.support.ConfigurableConversionService;

public interface ConfigurablePropertyResolver extends PropertyResolver {

    ConfigurableConversionService getConversionService();

    void setConversionService(ConfigurableConversionService conversionService);
}
