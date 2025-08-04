package com.minis.beans.factory.config;

import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.utils.StringValueResolver;
import com.minis.utils.SystemPropertyUtils;

public abstract class PlaceholderConfigurerSupport implements BeanFactoryAware, BeanFactoryPostProcessor {

    // Default placeholder prefix: "${".
    public static final String DEFAULT_PLACEHOLDER_PREFIX = SystemPropertyUtils.PLACEHOLDER_PREFIX;

    // Default placeholder suffix: "}".
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = SystemPropertyUtils.PLACEHOLDER_SUFFIX;

    // Default value separator: ":".
    public static final String DEFAULT_VALUE_SEPARATOR = SystemPropertyUtils.VALUE_SEPARATOR;

    // Default escape character: {@code '\'}.
    public static final Character DEFAULT_ESCAPE_CHARACTER = SystemPropertyUtils.ESCAPE_CHARACTER;

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                       StringValueResolver valueResolver) {
        // Resolve placeholders in embedded values such as annotation attributes.
        beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
    }


}
