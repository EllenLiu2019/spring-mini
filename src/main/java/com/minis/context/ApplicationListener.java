package com.minis.context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EventListener;

public class ApplicationListener<E extends ApplicationEvent> implements EventListener {
    private static final Logger LOGGER = LogManager.getLogger(ApplicationListener.class.getName());
    public void onApplicationEvent(E event) {
        LOGGER.debug("Application Listener publish event: " + event);
    }
}