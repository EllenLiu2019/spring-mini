package com.minis.core.env;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {

    private final PropertySources propertySources;

    public PropertySourcesPropertyResolver(PropertySources propertySources) {
        this.propertySources = propertySources;
    }

    @Override
    public String getProperty(String key) {
        return getProperty(key, String.class, true);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return getProperty(key, targetType, true);
    }

    @Override
    protected String getPropertyAsRawString(String key) {
        return getProperty(key, String.class, false);
    }

    protected <T> T getProperty(String key, Class<T> targetValueType, boolean resolveNestedPlaceholders) {
        if (this.propertySources != null) {
            for (PropertySource<?> propertySource : this.propertySources) {
                log.trace("Searching for key '" + key + "' in PropertySource '" + propertySource.getName() + "'");

                Object value = propertySource.getProperty(key);
                if (value != null) {
                    if (resolveNestedPlaceholders) {
                        if (value instanceof String string) {
                            value = resolveNestedPlaceholders(string);
                        } else if ((value instanceof CharSequence cs) && (String.class.equals(targetValueType) ||
                                CharSequence.class.equals(targetValueType))) {
                            value = resolveNestedPlaceholders(cs.toString());
                        }
                    }

                    log.debug("Found key '" + key + "' in PropertySource '" + propertySource.getName() +
                            "' with value of type " + value.getClass().getSimpleName());
                    return convertValueIfNecessary(value, targetValueType);
                }
            }
        }
        log.trace("Could not find key '" + key + "' in any property source");
        return null;
    }
}
