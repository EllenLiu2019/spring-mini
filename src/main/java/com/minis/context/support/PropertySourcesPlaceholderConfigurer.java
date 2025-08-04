package com.minis.context.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.config.PlaceholderConfigurerSupport;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.context.EnvironmentAware;
import com.minis.core.env.*;
import com.minis.utils.StringValueResolver;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PropertySourcesPlaceholderConfigurer extends PlaceholderConfigurerSupport implements EnvironmentAware {

    public static final String ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME = "environmentProperties";

    private MutablePropertySources propertySources;

    private PropertySources appliedPropertySources;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.propertySources = new MutablePropertySources();
        if (this.environment != null && this.environment instanceof ConfigurableEnvironment configurableEnvironment) {
            PropertySource<?> environmentPropertySource = new ConfigurableEnvironmentPropertySource(configurableEnvironment);
            this.propertySources.addLast(environmentPropertySource);
        }

        processProperties(beanFactory, createPropertyResolver(this.propertySources));
        this.appliedPropertySources = this.propertySources;
    }

    protected ConfigurablePropertyResolver createPropertyResolver(MutablePropertySources propertySources) {
        return new PropertySourcesPropertyResolver(propertySources);
    }

    /**
     * TODO: Visit each bean definition in the given bean factory and attempt to
     *  replace ${...} property placeholders with values from the given properties.
     */
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                     ConfigurablePropertyResolver propertyResolver) throws BeansException {

        StringValueResolver valueResolver = strVal -> {
            String resolved = propertyResolver.resolveRequiredPlaceholders(strVal);
            return resolved;
        };

        // Resolve placeholders in embedded values such as annotation attributes.
        doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    private static class ConfigurableEnvironmentPropertySource extends PropertySource<ConfigurableEnvironment> {

        ConfigurableEnvironmentPropertySource(ConfigurableEnvironment environment) {
            super(ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME, environment);
        }


        @Override
        // Declare String as covariant return type, since a String is actually required.
        public String getProperty(String name) {
            for (PropertySource<?> propertySource : super.source.getPropertySources()) {
                Object candidate = propertySource.getProperty(name);
                if (candidate != null) {
                    return convertToString(candidate);
                }
            }
            return null;
        }

        private String convertToString(Object value) {
            return value.toString();
        }

        @Override
        public String toString() {
            return "ConfigurableEnvironmentPropertySource {propertySources=" + super.source.getPropertySources() + "}";
        }

        @Override
        public String[] getPropertyNames() {
            return new String[0];
        }
    }
}
