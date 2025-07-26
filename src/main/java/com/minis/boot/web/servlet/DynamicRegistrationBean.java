package com.minis.boot.web.servlet;

import jakarta.servlet.Registration;
import jakarta.servlet.ServletContext;

public abstract class DynamicRegistrationBean<D extends Registration.Dynamic> extends RegistrationBean {

    private static String name = "dispatcherServlet";

    private boolean asyncSupported = true;

    private String beanName;


    @Override
    protected final void register(String description, ServletContext servletContext) {
        D registration = addRegistration(description, servletContext);
        configure(registration);
    }

    protected void configure(D registration) {
        registration.setAsyncSupported(this.asyncSupported);
    }

    protected final String getOrDeduceName(Object value) {
        if (name != null) {
            return name;
        }
        return beanName;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    protected abstract D addRegistration(String description, ServletContext servletContext);
}
