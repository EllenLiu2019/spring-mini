package com.minis.boot.web.servlet.context;

import com.minis.beans.BeansException;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.boot.web.context.WebServerApplicationContext;
import com.minis.boot.web.servlet.ServletContextInitializer;
import com.minis.boot.web.servlet.server.ServletWebServerFactory;
import com.minis.web.context.WebApplicationContext;
import com.minis.web.context.support.GenericWebApplicationContext;
import com.minis.boot.web.servlet.server.WebServer;


import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServletWebServerApplicationContext extends GenericWebApplicationContext
        implements WebServerApplicationContext {
    public static final String DISPATCHER_SERVLET_NAME = "dispatcherServlet";

    private volatile WebServer webServer;

    private ServletConfig servletConfig;

    private String serverNamespace;

    public ServletWebServerApplicationContext() {
    }

    public ServletWebServerApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public final void refresh() throws IllegalStateException {
        try {
            super.refresh();
        } catch (RuntimeException ex) {
            WebServer webServer = this.webServer;
            if (webServer != null) {
                try {
                    webServer.stop();
                    webServer.destroy();
                } catch (RuntimeException stopOrDestroyEx) {
                    ex.addSuppressed(stopOrDestroyEx);
                }
            }
            throw ex;
        }
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        try {
            createWebServer();
        } catch (Throwable ex) {
            throw new RuntimeException("Unable to start web server", ex);
        }
    }

    private void createWebServer() {
        WebServer webServer = this.webServer;
        ServletContext servletContext = getServletContext();
        if (webServer == null && servletContext == null) {
            ServletWebServerFactory factory = getWebServerFactory();
            this.webServer = factory.getWebServer(getSelfInitializer());
            /*getBeanFactory().registerSingleton("webServerGracefulShutdown",
                    new WebServerGracefulShutdownLifecycle(this.webServer));
            getBeanFactory().registerSingleton("webServerStartStop",
                    new WebServerStartStopLifecycle(this, this.webServer));*/
        } else if (servletContext != null) {
            try {
                getSelfInitializer().onStartup(servletContext);
            } catch (ServletException ex) {
                throw new RuntimeException("Cannot initialize servlet context", ex);
            }
        }
    }

    private ServletContextInitializer getSelfInitializer() {
        return this::selfInitialize;
    }

    private void selfInitialize(ServletContext servletContext) throws ServletException {
        prepareWebApplicationContext(servletContext);
        //registerApplicationScope(servletContext);
        //WebApplicationContextUtils.registerEnvironmentBeans(getBeanFactory(), servletContext);
        /*for (ServletContextInitializer initializerBean : getServletContextInitializerBeans()) {
            initializerBean.onStartup(servletContext);
        }*/
    }


    /**
     * Prepare the {@link WebApplicationContext} with the given fully loaded
     * {@link ServletContext}. This method is usually called from
     * {@link ServletContextInitializer#onStartup(ServletContext)} and is similar to the
     * functionality usually provided by a {@link com.minis.web.context.ContextLoaderListener}.
     *
     * @param servletContext the operational servlet context
     */
    protected void prepareWebApplicationContext(ServletContext servletContext) {
        Object rootContext = servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (rootContext != null) {
            if (rootContext == this) {
                throw new IllegalStateException(
                        "Cannot initialize context because there is already a root application context present - "
                                + "check whether you have multiple ServletContextInitializers!");
            }
            return;
        }

        log.debug("Initializing Spring embedded WebApplicationContext");

        try {
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this);

            log.debug("Published root WebApplicationContext as ServletContext attribute with name ["
                    + WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE + "]");

            setServletContext(servletContext);

            long elapsedTime = System.currentTimeMillis() - getStartupDate();
            log.info("Root WebApplicationContext: initialization completed in " + elapsedTime + " ms");
        } catch (RuntimeException | Error ex) {
            log.error("Context initialization failed", ex);
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
            throw ex;
        }
    }


    private ServletWebServerFactory getWebServerFactory() {
        String[] beanNames = getBeanFactory().getBeanNamesForType(ServletWebServerFactory.class);
        try {
            return (ServletWebServerFactory) getBeanFactory().getBean(beanNames[0]);
        } catch (BeansException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        //beanFactory.addBeanPostProcessor(new WebApplicationContextServletContextAwareProcessor(this));
    }

}
