package com.minis.boot.web.servlet;

import com.minis.web.servlet.DispatcherServlet;

public class DispatcherServletRegistrationBean extends ServletRegistrationBean<DispatcherServlet> {

    private final String path = "/";

    public DispatcherServletRegistrationBean(DispatcherServlet servlet) {
        super(servlet);
        super.addUrlMappings(getServletUrlMapping());
    }

    public String getPath() {
        return this.path;
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
