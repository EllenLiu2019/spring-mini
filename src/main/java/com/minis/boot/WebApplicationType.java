package com.minis.boot;

import com.minis.utils.ClassUtils;

public enum WebApplicationType {
    /**
     * The application should not run as a web application and should not start an
     * embedded web server.
     */
    NONE,

    /**
     * The application should run as a servlet-based web application and should start an
     * embedded servlet web server.
     */
    SERVLET,

    /**
     * The application should run as a reactive web application and should start an
     * embedded reactive web server.
     */
    REACTIVE;

    private static final String[] SERVLET_INDICATOR_CLASSES = {"jakarta.servlet.Servlet", "com.minis.web.context.WebApplicationContext",};

    private static final String WEBMVC_INDICATOR_CLASS = "com.minis.web.servlet.DispatcherServlet";

    static WebApplicationType deduceFromClasspath() {
        if (!ClassUtils.isPresent(WEBMVC_INDICATOR_CLASS, null)) {
            return WebApplicationType.REACTIVE;
        }
        for (String className : SERVLET_INDICATOR_CLASSES) {
            if (!ClassUtils.isPresent(className, null)) {
                return WebApplicationType.NONE;
            }
        }
        return WebApplicationType.SERVLET;
    }
}
