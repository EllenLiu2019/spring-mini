package com.minis.boot;

import com.minis.context.ConfigurableApplicationContext;
import com.minis.core.io.SpringFactoriesLoader;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DefaultApplicationContextFactory implements ApplicationContextFactory {

    public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
        try {
            return getFromSpringFactories(webApplicationType, ApplicationContextFactory::create,
                    this::createDefaultApplicationContext);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable create a default ApplicationContext instance, "
                    + "you may need a custom ApplicationContextFactory", ex);
        }
    }

    private ConfigurableApplicationContext createDefaultApplicationContext() {
        return null;
    }

    private <T> T getFromSpringFactories(WebApplicationType webApplicationType,
                                         BiFunction<ApplicationContextFactory, WebApplicationType, T> action,
                                         Supplier<T> defaultResult) {
        for (ApplicationContextFactory candidate : SpringFactoriesLoader.loadFactories(ApplicationContextFactory.class, getClass().getClassLoader())) {
            T result = action.apply(candidate, webApplicationType);
            if (result != null) {
                return result;
            }
        }
        return (defaultResult != null) ? defaultResult.get() : null;
    }

}
