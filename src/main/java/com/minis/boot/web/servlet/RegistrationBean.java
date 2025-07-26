package com.minis.boot.web.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

public abstract class RegistrationBean implements ServletContextInitializer {

    @Override
    public final void onStartup(ServletContext servletContext) throws ServletException {
        String description = getDescription();
        register(description, servletContext);
    }

    protected abstract String getDescription();

    protected abstract void register(String description, ServletContext servletContext);
}
