package com.minis.web.context;

import com.minis.web.context.support.XmlWebApplicationContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ContextLoaderListener implements ServletContextListener {
    private static final Logger LOGGER = LogManager.getLogger(ContextLoaderListener.class.getName());
    public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
    private WebApplicationContext context;
    public ContextLoaderListener() {
        LOGGER.debug("ContextLoaderListener constructing");
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        LOGGER.debug("ContextLoaderListener initializing");
        initWebApplicationContext(event.getServletContext());
        LOGGER.debug("ContextLoaderListener initialed");
    }

    private void initWebApplicationContext(ServletContext servletContext) {
        LOGGER.debug("IoC WebApplication Context initializing");
        String contextLocation = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        WebApplicationContext webAppContext = new XmlWebApplicationContext(contextLocation);
        webAppContext.setServletContext(servletContext);
        this.context = webAppContext;
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}
