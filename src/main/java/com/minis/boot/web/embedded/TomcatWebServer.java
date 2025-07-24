package com.minis.boot.web.embedded;

import com.minis.boot.web.servlet.server.WebServer;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.util.concurrent.atomic.AtomicInteger;

public class TomcatWebServer implements WebServer {

    private static final AtomicInteger containerCounter = new AtomicInteger(-1);

    private final Tomcat tomcat;
    private volatile boolean started;
    public TomcatWebServer(Tomcat tomcat) {
        this.tomcat = tomcat;
        initialize();
    }

    private void initialize() {
        try {
            this.tomcat.start();
            this.startNonDaemonAwaitThread();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void start() throws RuntimeException {
        if (this.started) {
            return;
        }
    }

    @Override
    public void stop() throws RuntimeException {
        try {
            this.tomcat.stop();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    private void startNonDaemonAwaitThread() {
        Thread awaitThread = new Thread("container-" + (containerCounter.get())) {
            @Override
            public void run() {
                TomcatWebServer.this.tomcat.getServer().await();
            }

        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
    }
}
