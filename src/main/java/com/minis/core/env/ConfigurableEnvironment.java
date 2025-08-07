package com.minis.core.env;

import java.util.Map;

public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver
{

    Map<String, Object> getSystemProperties();

    Map<String, Object> getSystemEnvironment();

    MutablePropertySources getPropertySources();
}
