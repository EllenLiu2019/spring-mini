package com.minis.boot;

import com.minis.context.ConfigurableApplicationContext;
import com.minis.core.env.ConfigurableEnvironment;

public interface ApplicationContextFactory {

    ApplicationContextFactory DEFAULT = new DefaultApplicationContextFactory();

    ConfigurableApplicationContext create(WebApplicationType webApplicationType);

    default ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
        return null;
    }
}
