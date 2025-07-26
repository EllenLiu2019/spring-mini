package com.minis.boot.web.embedded;

import com.minis.boot.web.servlet.ServletContextInitializer;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * Servlet3.0 规范启动类
 */
@Slf4j
public class TomcatStarter implements ServletContainerInitializer {

    private final ServletContextInitializer[] initializers;

    private volatile Exception startUpException;

    TomcatStarter(ServletContextInitializer[] initializers) {
        this.initializers = initializers;
    }

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        try {
            for (ServletContextInitializer initializer : this.initializers) {
                initializer.onStartup(servletContext);
            }
        } catch (Exception ex) {
            this.startUpException = ex;
            // Prevent Tomcat from logging and re-throwing when we know we can
            // deal with it in the main thread, but log for information here.
            log.error("Error starting Tomcat context. Exception: " + ex.getClass().getName() + ". Message: "
                    + ex.getMessage());
        }
    }

    Exception getStartUpException() {
        return this.startUpException;
    }
}
