package com.minis;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class Main {

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        Context context = tomcat.addWebapp("/mvc-mini", new File("src/main/webapp/").getAbsolutePath());
        Connector connector = new Connector();
        connector.setPort(8088);
        tomcat.setConnector(connector);
        tomcat.start();
        tomcat.getServer().await();
    }
}
