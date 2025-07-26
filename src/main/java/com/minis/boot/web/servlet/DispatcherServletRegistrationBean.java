package com.minis.boot.web.servlet;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.web.servlet.DispatcherServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

public class DispatcherServletRegistrationBean extends ServletRegistrationBean<DispatcherServlet> {

    @Autowired
    private DispatcherServlet servlet;

    private final String path = "/";

    public DispatcherServletRegistrationBean() {
        super.addUrlMappings(getServletUrlMapping());
    }


    public String getPath() {
        return this.path;
    }

    public DispatcherServlet getServlet() {
        return this.servlet;
    }

    protected ServletRegistration.Dynamic addRegistration(String description, ServletContext servletContext) {
        String name = getServletName();
        return servletContext.addServlet(name, this.servlet);
    }

    String getServletUrlMapping() {
        if (getPath().isEmpty() || getPath().equals("/")) {
            return "/";
        }
        if (getPath().contains("*")) {
            return getPath();
        }
        if (getPath().endsWith("/")) {
            return getPath() + "*";
        }
        return getPath() + "/*";
    }


}
