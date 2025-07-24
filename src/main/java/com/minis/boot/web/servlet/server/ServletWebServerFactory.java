package com.minis.boot.web.servlet.server;

import com.minis.boot.web.servlet.ServletContextInitializer;

public interface ServletWebServerFactory {

    WebServer getWebServer(ServletContextInitializer... initializers);
}
