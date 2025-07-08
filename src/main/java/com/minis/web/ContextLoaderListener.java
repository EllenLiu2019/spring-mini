package com.minis.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;


public class ContextLoaderListener implements ServletContextListener {
    public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
    private WebApplicationContext context;
    public ContextLoaderListener() {
        System.out.println("---> construct ContextLoaderListener()");
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        initWebApplicationContext(event.getServletContext());
    }

    private void initWebApplicationContext(ServletContext servletContext) {
        String contextLocation = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        WebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext(contextLocation);
        webAppContext.setServletContext(servletContext);
        this.context = webAppContext;
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}
