package com.minis.boot.web.servlet.context;

import com.minis.boot.ApplicationContextFactory;
import com.minis.boot.WebApplicationType;
import com.minis.context.ConfigurableApplicationContext;

public class ServletWebServerApplicationContextFactory implements ApplicationContextFactory {

    public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
        return (webApplicationType != WebApplicationType.SERVLET) ? null : createContext();
    }

    private ConfigurableApplicationContext createContext() {
        return new AnnotationConfigServletWebServerApplicationContext();
    }
}
