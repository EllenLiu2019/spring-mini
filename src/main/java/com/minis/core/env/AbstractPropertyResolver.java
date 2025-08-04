package com.minis.core.env;

import com.minis.utils.ClassUtils;
import com.minis.utils.PropertyPlaceholderHelper;
import com.minis.utils.SystemPropertyUtils;


public abstract class AbstractPropertyResolver implements ConfigurablePropertyResolver {

    private String placeholderPrefix = SystemPropertyUtils.PLACEHOLDER_PREFIX;

    private String placeholderSuffix = SystemPropertyUtils.PLACEHOLDER_SUFFIX;

    private String valueSeparator = SystemPropertyUtils.VALUE_SEPARATOR;

    private Character escapeCharacter = SystemPropertyUtils.ESCAPE_CHARACTER;

    private PropertyPlaceholderHelper strictHelper;


    @Override
    public String getProperty(String key) {
        return getProperty(key, String.class);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null ? value : defaultValue);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        T value = getProperty(key, targetType);
        return (value != null ? value : defaultValue);
    }

    protected String resolveNestedPlaceholders(String value) {
        if (value.isEmpty()) {
            return value;
        }
        return resolveRequiredPlaceholders(value);
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        if (this.strictHelper == null) {
            this.strictHelper = createPlaceholderHelper(false);
        }
        return doResolvePlaceholders(text, this.strictHelper);
    }

    private PropertyPlaceholderHelper createPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {
        return new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix,
                this.valueSeparator, this.escapeCharacter, ignoreUnresolvablePlaceholders);
    }

    private String doResolvePlaceholders(String text, PropertyPlaceholderHelper helper) {
        return helper.replacePlaceholders(text, this::getPropertyAsRawString);
    }

    protected <T> T convertValueIfNecessary(Object value, Class<T> targetType) {
        if (targetType == null) {
            return (T) value;
        }
        if (ClassUtils.isAssignableValue(targetType, value)) {
            return (T) value;
        }
        return null;
    }

    protected abstract String getPropertyAsRawString(String key);
}
