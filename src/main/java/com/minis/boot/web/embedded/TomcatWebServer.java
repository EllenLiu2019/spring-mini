package com.minis.boot.web.embedded;

import com.minis.boot.web.servlet.server.WebServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.loader.ParallelWebappClassLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.naming.ContextBindings;

import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

@Slf4j
public class TomcatWebServer implements WebServer {

    private static final AtomicInteger containerCounter = new AtomicInteger(-1);

    private final Object monitor = new Object();

    private final Map<Service, Connector[]> serviceConnectors = new HashMap<>();

    private final Tomcat tomcat;
    private volatile boolean started;

    public TomcatWebServer(Tomcat tomcat) {
        this.tomcat = tomcat;
        initialize();
    }

    private void initialize() {
        log.info("Tomcat initialized with " + getPortsDescription(false));
        synchronized (this.monitor) {
            try {
                addInstanceIdToEngineName();

                Context context = this.findContext();

                this.tomcat.start();

                // We can re-throw failure exception directly in the main thread
                rethrowDeferredStartupExceptions();

                try {
                    ContextBindings.bindClassLoader(context, context.getNamingToken(), getClass().getClassLoader());
                } catch (NamingException ex) {
                    // Naming is not enabled. Continue
                }

                this.startNonDaemonAwaitThread();
            } catch (Exception ex) {
                this.stopSilently();
                this.destroySilently();
                throw new RuntimeException("Unable to start embedded Tomcat", ex);
            }
        }
    }

    private Context findContext() {
        for (Container child : this.tomcat.getHost().findChildren()) {
            if (child instanceof Context context) {
                return context;
            }
        }
        throw new IllegalStateException("The host does not contain a Context");
    }


    private void rethrowDeferredStartupExceptions() throws Exception {
        Container[] children = this.tomcat.getHost().findChildren();
        for (Container container : children) {
            if (container instanceof TomcatEmbeddedContext embeddedContext) {
                TomcatStarter tomcatStarter = embeddedContext.getStarter();
                if (tomcatStarter != null) {
                    Exception exception = tomcatStarter.getStartUpException();
                    if (exception != null) {
                        throw exception;
                    }
                }
            }
            if (!LifecycleState.STARTED.equals(container.getState())) {
                throw new IllegalStateException(container + " failed to start");
            }
        }
    }

    private void stopSilently() {
        try {
            this.stopTomcat();
        } catch (LifecycleException ex) {
            // Ignore
        }
    }

    private void destroySilently() {
        try {
            this.tomcat.destroy();
        } catch (LifecycleException ex) {
            // Ignore
        }
    }

    private void stopTomcat() throws LifecycleException {
        if (Thread.currentThread().getContextClassLoader() instanceof ParallelWebappClassLoader) {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        }
        this.tomcat.stop();
    }

    private void addInstanceIdToEngineName() {
        int instanceId = containerCounter.incrementAndGet();
        if (instanceId > 0) {
            Engine engine = this.tomcat.getEngine();
            engine.setName(engine.getName() + "-" + instanceId);
        }
    }

    private String getPortsDescription(boolean localPort) {
        StringBuilder description = new StringBuilder();
        Connector[] connectors = this.tomcat.getService().findConnectors();
        description.append("port");
        if (connectors.length != 1) {
            description.append("s");
        }
        description.append(" ");
        for (int i = 0; i < connectors.length; i++) {
            if (i != 0) {
                description.append(", ");
            }
            Connector connector = connectors[i];
            int port = localPort ? connector.getLocalPort() : connector.getPort();
            description.append(port).append(" (").append(connector.getScheme()).append(')');
        }
        return description.toString();
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
