package com.minis.boot.web.servlet.server;

public interface WebServer {

    void start() throws RuntimeException;

    void stop() throws RuntimeException;

    default void destroy() {
        stop();
    }
}
