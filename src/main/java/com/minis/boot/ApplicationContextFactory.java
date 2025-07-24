package com.minis.boot;

import com.minis.context.ConfigurableApplicationContext;

public interface ApplicationContextFactory {

    ApplicationContextFactory DEFAULT = new DefaultApplicationContextFactory();

    ConfigurableApplicationContext create(WebApplicationType webApplicationType);
}
