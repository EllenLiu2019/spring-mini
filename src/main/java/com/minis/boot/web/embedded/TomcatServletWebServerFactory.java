package com.minis.boot.web.embedded;

import com.minis.boot.web.servlet.ServletContextInitializer;
import com.minis.boot.web.servlet.server.ServletWebServerFactory;
import com.minis.boot.web.servlet.server.WebServer;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class TomcatServletWebServerFactory implements ServletWebServerFactory {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Set<Class<?>> NO_CLASSES = Collections.emptySet();

    private int port = 8080;

    private static String protocol = "org.apache.coyote.http11.Http11NioProtocol";

    private Charset uriEncoding = DEFAULT_CHARSET;

    private int backgroundProcessorDelay = 10;

    public WebServer getWebServer(ServletContextInitializer... initializers) {
        Tomcat tomcat = new Tomcat();
        File baseDir = createTempDir("tomcat");
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        Connector connector = new Connector(this.protocol);
        connector.setThrowOnFailure(true);
        tomcat.getService().addConnector(connector);
        customizeConnector(connector);
        tomcat.setConnector(connector);
        tomcat.getHost().setAutoDeploy(false);
        configureEngine(tomcat.getEngine());
        prepareContext(tomcat.getHost(), initializers);
        return getTomcatWebServer(tomcat);
    }


    private void prepareContext(Host host, ServletContextInitializer[] initializers) {
        TomcatEmbeddedContext context = new TomcatEmbeddedContext();
        WebResourceRoot resourceRoot = new StandardRoot(context);
        ignoringNoSuchMethodError(() -> resourceRoot.setReadOnly(true));
        context.setResources(resourceRoot);
        context.setName("");
        context.setDisplayName("application");
        context.setPath("");
        File docBase = createTempDir("tomcat-docbase");
        context.setDocBase(docBase.getAbsolutePath());
        context.addLifecycleListener(new Tomcat.FixContextListener());
        context.setCreateUploadTargets(true);
        host.addChild(context);
        configureContext(context, initializers);
    }

    private void ignoringNoSuchMethodError(Runnable method) {
        try {
            method.run();
        }
        catch (NoSuchMethodError ex) {
        }
    }

    private void configureContext(Context context, ServletContextInitializer[] initializers) {
        TomcatStarter starter = new TomcatStarter(initializers);
        if (context instanceof TomcatEmbeddedContext embeddedContext) {
            embeddedContext.setStarter(starter);
            embeddedContext.setFailCtxIfServletStartFails(true);
        }
        context.addServletContainerInitializer(starter, NO_CLASSES);
    }


    private void customizeConnector(Connector connector) {
        int port = Math.max(getPort(), 0);
        connector.setPort(port);
        if (getUriEncoding() != null) {
            connector.setURIEncoding(getUriEncoding().name());
        }
    }

    public Charset getUriEncoding() {
        return this.uriEncoding;
    }

    private void configureEngine(Engine engine) {
        engine.setBackgroundProcessorDelay(this.backgroundProcessorDelay);
    }

    protected final File createTempDir(String prefix) {
        try {
            File tempDir = Files.createTempDirectory(prefix + "." + getPort() + ".").toFile();
            tempDir.deleteOnExit();
            return tempDir;
        }
        catch (IOException ex) {
            throw new RuntimeException(
                    "Unable to create tempDir. java.io.tmpdir is set to " + System.getProperty("java.io.tmpdir"), ex);
        }
    }

    public int getPort() {
        return this.port;
    }

    protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
        return new TomcatWebServer(tomcat);
    }

}
